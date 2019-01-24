package com.lwl.dataSource;

import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.lwl.enums.RouteDataSourceKeyEnum;

/**
 * mybatis拓展插件，其实就是过滤器，这里会设置数据源
 * 	为什么这里只有update 和 query 2个方法呢？ 我们看一下Executor接口提供的方法就知道，除了这2个方法之外，其他的方法都是些补救方法，比如回滚，关闭流，提交，事务
 * @author lwl
 * @create 2019年1月23日 下午1:14:49
 * @version 1.0
 */
@Intercepts({ 
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) 
})

public class RouteDataSourcePlugin implements Interceptor {

	protected static final Logger logger = LoggerFactory.getLogger(RouteDataSourcePlugin.class);
	
	//这个是用于项目中自己定义的方法，如果包含这些，则使用主数据源，因为这些操作将改变数据库状态
	private static final String REGEX = ".*insert\\u0020.*|.*delete\\u0020.*|.*update\\u0020.*";

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		//是否使用了事务
		 //它首先查看当前是否存在事务管理上下文，并尝试从事务管理上下文获取连接，如果获取失败，直接从数据源中获取连接。
        //在获取连接后，如果当前拥有事务上下文，则将连接绑定到事务上下文中。（此处直接继续下一过程）
		boolean synchronizationActive  = TransactionSynchronizationManager.isSynchronizationActive();
		
		//默认设置主数据源  ，设置数据源
		RouteDataSourceKeyEnum selectKey = RouteDataSourceKeyEnum.MASTER;
		
		//获取当前执行的参数 
		Object[] args = invocation.getArgs();
		
		MappedStatement ms = (MappedStatement) args[0];
		
		//如果未采用事务
		if(!synchronizationActive) {
			//如果是查询语句,而且不是查询主键的方法（部分insert方法执行时候，会调用selectKey方法，将主键插入）
			if(ms.getSqlCommandType().equals(SqlCommandType.SELECT)&&!ms.getId().contains(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
				
				BoundSql boundSql = ms.getSqlSource().getBoundSql(args[1]);
				String sql = boundSql.getSql().toLowerCase(Locale.CHINA).replace("[\\t\\n\\r]", " ");
				if (!sql.matches(REGEX)) {
					selectKey = RouteDataSourceKeyEnum.SLAVE;// 读使用从库
				}  
			}
		}
		logger.info("---------------------------------------------");
		logger.info("\n\n调用的方法是：{} 选择的数据源是：{} 执行的方法类型是：{} \n\n", ms.getId(),selectKey.name(),ms.getSqlCommandType().name());
		logger.info("---------------------------------------------");
		
		//切换数据源
		DatabaseContextHolder.setDataSource(selectKey);
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		//这里其实是生成一个代理对象
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

 

}
