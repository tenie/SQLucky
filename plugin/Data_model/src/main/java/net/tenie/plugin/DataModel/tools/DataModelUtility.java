package net.tenie.plugin.DataModel.tools;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.alibaba.fastjson.JSONObject;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.LoadingAnimation;
import net.tenie.Sqlucky.sdk.db.PoDao;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.plugin.DataModel.DataModelTabTree;
import net.tenie.plugin.DataModel.po.DataModelInfoMapper;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTableFieldsPo;
import net.tenie.plugin.DataModel.po.DataModelTablePo;
import net.tenie.plugin.DataModel.po.DataModelTreeNodePo;

public class DataModelUtility {
	
	public static void modelFileImport(String encode) {
		// 获取文件
		File f = FileOrDirectoryChooser.showOpenJsonFile("Open", ComponentGetter.primaryStage);
		if (f == null) {
			return;
		}else {
			var sceneRoot = ComponentGetter.primarySceneRoot;
			
			// 载入动画
			LoadingAnimation.addLoading(sceneRoot, "Saving....");
//			后台执行 数据导入
			CommonUtility.runThread(v->{
				try {
					// 读取
					DataModelInfoPo DataModelPoVal = readJosnModel(encode, f);
					var mdpo = DataModelDAO.selectDMInfoByName( DataModelPoVal.getName());
					//同名模型存在
					if(mdpo !=null ) {
						MyAlert.errorAlert("Duplicates model name:" +DataModelPoVal.getName() +"; modify exist model name");
					}
					
					// 数据插入到数据库
					Long  mid = DataModelUtility.insertDataModel(DataModelPoVal);
					// 插入模型节点
					DataModelUtility.addModelItem( mid, DataModelTabTree.treeRoot);
				} catch (IOException e) {
					e.printStackTrace();
					MyAlert.errorAlert(e.getMessage());
				}finally {
					// 移除动画
					LoadingAnimation.rmLoading(sceneRoot);
				}
			});
		}
	}
	// 保存按钮触发保存数据操作
	public static void saveDataModelToDB(StackPane wdroot , DataModelInfoPo val, Consumer< String >  caller) {
		// 载入动画
		LoadingAnimation.addLoading(wdroot, "Saving....");
//		后台执行 数据导入
		CommonUtility.runThread(v -> {
			try {
				// 数据插入到数据库
				Long mid = DataModelUtility.insertDataModel(val);
				// 插入模型节点
				DataModelUtility.addModelItem(mid, DataModelTabTree.treeRoot);
				caller.accept("");
			} catch (Exception e) {
				e.printStackTrace();
				MyAlert.errorAlert(e.getMessage());
			} finally {
				// 移除动画
				LoadingAnimation.rmLoading(wdroot);
			}
		});
	}
	
	
	// 从文件中读取 数据
	public static DataModelInfoPo readModelInfo(String encode, File f) throws IOException {
		DataModelInfoPo DataModelPoVal = readJosnModel(encode, f);
		return DataModelPoVal;
	}
	
	// 读取josn 的模型文件
	public static DataModelInfoPo readJosnModel(String encode, File f) throws IOException {
		String val = "";
		DataModelInfoPo DataModelPoVal = null;
		val = FileUtils.readFileToString(f, encode);
		if (val != null && !"".equals(val)) {
		   DataModelPoVal = JSONObject.parseObject(val, DataModelInfoPo.class);
		}
		return DataModelPoVal;
	}

	// 模型插入到数据库
	public static Long insertDataModel(DataModelInfoPo dmp) {
		var conn = SqluckyAppDB.getConnNotAutoCommit();
		
		Long modelID = -1L;
		try {  
		    modelID = PoDao.insertReturnID(conn, dmp);

			var tables = dmp.getEntities();
			for (var tab : tables) {
				tab.setModelId(modelID);
				var tableId = PoDao.insertReturnID(conn, tab);

				// 字段
				var fields = tab.getFields();
				for (var field : fields) {
					System.out.println(field);
					field.setTableId(tableId);
					field.setModelId(modelID);
					field.setCreatedTime(new Date());
					PoDao.insert(conn, field);
				}

			}
			SqluckyAppDB.closeConnAndCommit(conn);
		} catch (Exception e) {
			SqluckyAppDB.closeConnAndRollback(conn);
			e.printStackTrace();
		} 
		return modelID;
	}
	
	// 添加模型item
	public static void addModelItem( Long modelID, TreeItem<DataModelTreeNodePo> treeRoot ) {
		DataModelInfoPo mpo = DataModelDAO.selectDMInfo(modelID);
		DataModelTreeNodePo nodepo = new DataModelTreeNodePo(mpo);
		TreeViewAddModelItem(treeRoot, nodepo);
	}
	
	public static void refreshTreeView() {
		
	}
	
	
	/**
	 * 删除模型Action
	 * 1. 先获取选择的模型, 判断是不是模型
	 * 2. 回调函数中删除
	 * 3. 回调函数传给确认提醒, 用户确认后才会调用删除回调函数
	 */
	public static void delAction() {
		var item = DataModelTabTree.currentSelectItem();
		if ( item.getValue().getIsModel()) {
			Consumer<String> caller = x -> {
				DataModelUtility.delModel(DataModelTabTree.treeRoot, item);
			};
			MyAlert.myConfirmation(" Delete Model : " + item.getValue().getName() + "?", caller);
			
		}
	}
	/**
	 * 删除模型
	 * @param root
	 * @param model
	 */
	public static void delModel(TreeItem<DataModelTreeNodePo>  root, TreeItem<DataModelTreeNodePo> model) {
		var delSuccess = delModelItem(root, model);
		if(delSuccess) {
			delModelData(model.getValue().getModelId());
		}
		
	}

	//tree 上删除模型的item
	public static boolean delModelItem(TreeItem<DataModelTreeNodePo>  root, TreeItem<DataModelTreeNodePo> model) {
		boolean delSuccess = false;
		var chs = root.getChildren();
		var po = model.getValue();
		if( po.getIsModel() ) {
			chs.remove(model);
			delSuccess = true;
		}
		
		return delSuccess;
	}
	// 数据库里删除模型数据
	public static void delModelData(Long mid) {
		var conn = SqluckyAppDB.getConn();
		try {
			DataModelInfoPo model = new DataModelInfoPo();
			model.setId(mid);
			PoDao.delete(conn, model);
			
			DataModelTablePo table = new DataModelTablePo();
			table.setModelId(mid);
			PoDao.delete(conn, table);
			
			DataModelTableFieldsPo field = new DataModelTableFieldsPo();
			field.setModelId(mid);
			PoDao.delete(conn, field);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SqluckyAppDB.closeConn(conn);
		}
		
		
		
	}
	
	
	
	

//	//TODO 恢复数据中保存的连接数据
	public static void recoverModelInfoNode(TreeItem<DataModelTreeNodePo> rootNode) {

		Consumer<String> cr = v -> {
			List<DataModelInfoPo> ls = DataModelDAO.selectDMInfo();
			if(ls !=null && ls.size()>0) {
				for(var po : ls) {
					DataModelTreeNodePo nodepo = new DataModelTreeNodePo(po);
					TreeViewAddModelItem(rootNode, nodepo);
//					TreeItem<DataModelTreeNodePo> item = createItemNode(nodepo);
//					Platform.runLater(() -> {
//						rootNode.getChildren().add(item);
//					});
				}
			}
			
			 
		};
		CommonUtility.addInitTask(cr);

	}
	
	
	public static void TreeViewAddModelItem(TreeItem<DataModelTreeNodePo> rootNode, DataModelTreeNodePo nodepo) {
		TreeItem<DataModelTreeNodePo> item = createItemNode(nodepo);
		Platform.runLater(() -> {
			rootNode.getChildren().add(item);
		});
	}
	/**
	 * 创建 一个节点
	 * @param name
	 * @return
	 */
	public static TreeItem<DataModelTreeNodePo> createItemNode(DataModelTreeNodePo treeNode) {
		Region icon = null;
		if (treeNode.getIsModel()) {
			icon = IconGenerator.svgImageUnactive("database");;
			Region acIcon = IconGenerator.svgImage("database", "#7CFC00 ");
//			treeNode.setIcon(icon);
			treeNode.setUnactiveIcon(icon);
			treeNode.setActiveIcon(acIcon);
		} else {
			icon = IconGenerator.svgImage("window-restore", "blue");
//			treeNode.setIcon(icon);
			treeNode.setUnactiveIcon(icon);
			treeNode.setActiveIcon(icon);
		}

		TreeItem<DataModelTreeNodePo> item = new TreeItem<>(treeNode);
		item.getChildren().addListener(new ListChangeListener<TreeItem<DataModelTreeNodePo>>() {
			@Override
			public void onChanged(Change c) {
				var list = c.getList();
				System.out.println("list.size() = " + list.size());
				if(list.size() > 0) {
					treeNode.setActive(true);
				}
				
			}});
		return item;
	}
	
	
	/**
	 * 根据模型id, 给模型重命名
	 * @param mid
	 * @param newName
	 * @return
	 */
	public static void renameModelName(  ) {
		var mdpo = DataModelTabTree.currentSelectItem().getValue();
		Long mid = mdpo.getModelId();
		Consumer<String> caller = newName -> {
			if (StrUtils.isNullOrEmpty( newName.trim()))
				return;
			var val = DataModelDAO.selectDMInfoByName(newName);
			if(val != null ) { 
				MyAlert.errorAlert("Fail ! Name Exist :" + newName);
			}else {
				DataModelDAO.updateModelName(mid, newName);
				mdpo.setName(newName);
				DataModelTabTree.DataModelTreeView.refresh();
			}
			 
		};
		ModalDialog.showExecWindow("New name ","", caller);
	}
	
	
	public static void closeModel() {
		var item = DataModelTabTree.currentSelectItem();
		var itemPo = item.getValue();
		if(itemPo.getIsModel()) {
			item.getChildren().clear();
			itemPo.setActive(false);
			DataModelTabTree.DataModelTreeView.refresh();
		}
	}
	
	// 测试PoDao
	public static void test() {
		var conn = SqluckyAppDB.getConn();
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
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

	}

	// mybites 测试
	public static void test2() {
//		 SqlSessionFactory sqlSessionFactory2= new SqlSessionFactoryBuilder().build

//		DataSource dataSource = SqluckyAppDB.getH2DataSource();
		DataSource dataSource = SqluckyAppDB.getSqliteDataSource();
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
		DataSource dataSource = SqluckyAppDB.getSqliteDataSource();
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
