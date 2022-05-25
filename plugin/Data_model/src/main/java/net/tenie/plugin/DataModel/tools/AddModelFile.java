package net.tenie.plugin.DataModel.tools;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import net.tenie.Sqlucky.sdk.db.connection.SqluckyConnection;
import net.tenie.Sqlucky.sdk.po.tools.PoDao;
import net.tenie.plugin.DataModel.po.DataModelInfoMapper;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;

public class AddModelFile {
	
	public static void insert(DataModelInfoPo dmp) {
		
		var tabs = dmp.getEntities();
		
		
	}
	public static void test()   {
		var conn = SqluckyConnection.getConn();
		try {
			DataModelInfoPo poinsert = new DataModelInfoPo();
			poinsert.setName("insertName");
			poinsert.setDescribe("ins...");
			poinsert.setAvatar("insaaa");
			PoDao.insert(conn, poinsert);
			
			
			DataModelInfoPo po = new DataModelInfoPo();
			po.setId(2L);
			List<DataModelInfoPo> val;
			
			val = PoDao.select(conn, po);
			System.out.println(val);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void test2() {
//		 SqlSessionFactory sqlSessionFactory2= new SqlSessionFactoryBuilder().build
		
		DataSource dataSource = SqluckyConnection.getH2DataSource();
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DataModelInfoMapper.class);
//		configuration.addMappedStatement(null);
		configuration.addMappedStatement(null);
		
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		
		try (SqlSession session = sqlSessionFactory.openSession()) {
			DataModelInfoMapper mapper = session.getMapper(DataModelInfoMapper.class);
			DataModelInfoPo val = mapper.selectDataModelInfo(1);
			System.out.println(val);
			}
	}
	
	public static void main(String[] args) throws IOException {

		DataSource dataSource = SqluckyConnection.getH2DataSource();
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(DataModelInfoPo.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		
		
		try (SqlSession session = sqlSessionFactory.openSession()) {
			DataModelInfoMapper mapper = session.getMapper(DataModelInfoMapper.class);
			DataModelInfoPo val = mapper.selectDataModelInfo(11);
			System.out.println(val);
			}

	}
}
