package net.tenie.plugin.codeGeneration.utility;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.tenie.Sqlucky.sdk.db.PoDaoUtil;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;

public class CreateInfoservicePoBean {

	private String poClassName = "";
	private String poClassFileName = "";
	private String poTxt = "";
    private static final int TYPE_ATTRNAME = 2;

    private static final int TYPE_CLSNAME = 1;

    private String attrDecl = "\tprivate {1} {0};\n";

    private String clsTemp = "@SuppressWarnings(\"serial\")\n" + "public class {0}PO implements DataBean<Object>'{'\n";

    private String comment = " ";


    private String genPath = null;

    //get方法模版
    private String getTemp = "\tpublic {1} get{0}()'{'\n" + "\t\treturn this.{2};\n" + "\t}\n";

    private String headTemp = "package {0};\n\n" + "{1,choice,0#|1#import java.util.Date;\n}" + "import com.infoservice.po.DataBean;\n"
            + "import com.infoservice.po.POFactoryUtil;\n";

    //package and import
    private int impDate = 1;

    private String packName = "com.infoservice.dms.po";

    //set方法模版
    private String setTemp = "\tpublic void set{0}({1} {2})'{'\n" + "\t\tthis.{2}={2};\n" + "\t}\n";

    private LinkedList<String> tabNames = new LinkedList<String>();

    private String toXml = "\tpublic String toXMLString(){\n" + "\t\treturn POFactoryUtil.beanToXmlString(this);\n" + "\t}\n";

	public String createPo(String tab, Connection conn) throws Exception {
		String rsVal = "";
		Statement ste = null;
		ResultSet rs = null;
		try {
			ste = conn.createStatement();
			String query = "select   *   from   " + tab + "    where   1=2 ";
			rs = ste.executeQuery(MessageFormat.format(query, tab));
			HashMap<String, Class> infos = PoDaoUtil.getResultSetMetaData(rs);

			rsVal = genPOFile(tab, infos);

		} catch (Exception e) {
			e.printStackTrace();
			MyAlert.errorAlert(e.toString());
		} finally {
			rs.close();
			ste.close();
		}
		return rsVal;
	}

    //生成set 和 get 方法
    private void genMethod(StringBuilder sbd, String attrName, Class clsType) {
        try {
            String attrName1 = this.tabName2PoName(CreateInfoservicePoBean.TYPE_CLSNAME, attrName);
            String attrName2 = this.tabName2PoName(CreateInfoservicePoBean.TYPE_ATTRNAME, attrName);
            sbd.append(MessageFormat.format(this.setTemp, attrName1, clsType.getSimpleName(), attrName2));
            sbd.append("\n");
            sbd.append(MessageFormat.format(this.getTemp, attrName1, clsType.getSimpleName(), attrName2));
            sbd.append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String genPOFile(String tabName, HashMap<String, Class> infos) {
    	String rsval  = "";
        try {
            if (infos.containsValue(java.util.Date.class)) {
                this.impDate = 1;
            } else {
                this.impDate = 0;
            }
            StringBuilder sbd = new StringBuilder();
            sbd.append(MessageFormat.format(this.comment, new Date(), System.getenv("USERNAME")));
            sbd.append("\n");
            sbd.append(MessageFormat.format(this.headTemp, this.packName, this.impDate));
            sbd.append("\n");
            poClassName = this.tabName2PoName(CreateInfoservicePoBean.TYPE_CLSNAME, tabName);
            sbd.append(MessageFormat.format(this.clsTemp, poClassName ));
            sbd.append("\n");

            Iterator<String> ite = infos.keySet().iterator();
            String key = null;

            while (ite.hasNext()) {
                key = ite.next();
                sbd.append(MessageFormat.format(this.attrDecl, this.tabName2PoName(CreateInfoservicePoBean.TYPE_ATTRNAME, key), infos.get(key).getSimpleName()));
            }
            sbd.append("\n");

            ite = infos.keySet().iterator();
            while (ite.hasNext()) {
                key = ite.next();
                genMethod(sbd, key, infos.get(key));
            }

            sbd.append(this.toXml);

            sbd.append("}");

//            writePOFile(tabName, sbd);
//            System.out.println(sbd);
            rsval = sbd.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        poTxt = rsval;
        return rsval;
    }
 

    private String tabName2PoName(int type, String tname) {
        byte ascii = 'Z' - 'z';
        tname = tname.toLowerCase();
        StringBuilder sbd = new StringBuilder();
        for (int i = 0; i < tname.length(); i++) {
            if (tname.charAt(i) == '_' && i + 1 < tname.length()) {
                i++;
                if (tname.charAt(i) >= 'a' && tname.charAt(i) <= 'z') {
                    sbd.append((char) (tname.charAt(i) + ascii));
                } else {
                    sbd.append(tname.charAt(i));
                }
            } else {
                sbd.append(tname.charAt(i));
            }
        }
        if (type == CreateInfoservicePoBean.TYPE_CLSNAME) {
            if (sbd.charAt(0) >= 'a' && sbd.charAt(0) <= 'z')
                sbd.setCharAt(0, (char) (sbd.charAt(0) + ascii));
        }
        return sbd.toString();
    }

    //写文件
    private void writePOFile(String tabName, StringBuilder sbd) {
        try {
            String file = this.genPath + '/' + this.packName.replace('.', '/') + '/';
            File dir = new File(file);
            if (!dir.exists())
                dir.mkdirs();
            file += this.tabName2PoName(CreateInfoservicePoBean.TYPE_CLSNAME, tabName) + "PO.java";

//            System.out.println("Write Class File : " + file);
            FileWriter fw = new FileWriter(file);
            fw.write(sbd.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            // System.exit(0);
        }
    }

	public String getPoClassName() {
		return poClassName;
	}

	public void setPoClassName(String poClassName) {
		this.poClassName = poClassName;
	}

	public String getPoTxt() {
		return poTxt;
	}

	public void setPoTxt(String poTxt) {
		this.poTxt = poTxt;
	}

	public String getPoClassFileName() {
		return poClassName   + "PO.java" ;
	}


}

