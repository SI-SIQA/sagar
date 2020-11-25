package com.infacloud.test.ui.application.mct_cmd.PDO_Snowflake;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.io.Files;
import com.infacloud.framework.actions.ActionResult;
import com.infacloud.framework.actions.SaasUARestImpl;
import com.infacloud.framework.actions.SaasUAUIImpl;
import com.infacloud.framework.common.ILogger;
import com.infacloud.framework.common.UTFConfiguration;
import com.infacloud.framework.common.ILogger.MESSAGE_LEVEL;
import com.infacloud.framework.dataprovider.CloudContext;
import com.infacloud.framework.driver.web.ICloudWebDriver;
import com.infacloud.framework.model.ICloudModel;
import com.infacloud.framework.model.enums.CONNECTION_TYPE_ABBREVIATED;
import com.infacloud.framework.model.enums.SERVICE_TYPE;
import com.infacloud.framework.model.enums.SOURCE_TYPE;
import com.infacloud.framework.model.enums.TX_TYPE;
import com.infacloud.framework.model.saas.application.dss.DSSDataModel;
import com.infacloud.framework.model.saas.application.mct.MCTDataModel;
import com.infacloud.framework.model.saas.configuration.connection.IConnectionMdl;
import com.infacloud.framework.model.saas.design.mappings.CMDModel;
import com.infacloud.framework.model.saas.design.mappings.transformations.ITxMdl;
import com.infacloud.framework.model.saas.design.mappings.transformations.TargetTxMdl;
import com.infacloud.framework.pageobject.saas.CloudApplication;
import com.infacloud.framework.sdk.CloudDriverException;
import com.infacloud.framework.sdk.CloudUTF;
import com.infacloud.framework.sdk.dbconnection.OracleConnection;
import com.infacloud.framework.sdk.dbconnection.SqlServerConnection;
import com.infacloud.framework.sdk.dbconnection.exceptions.NotSupportedException;
import com.infacloud.framework.sdk.dbconnection.exceptions.ParseException;
import com.infacloud.test.ui.application.mct_cmd.PDO_Snowflake.SerachStringLog;
import com.infacloud.utils.FileUtils;
import com.infacloud.utils.SessionLogParser;
import com.infacloud.utils.Utils;

import java.io.FileWriter; 
public class PDO_SnowflakeDW_Incoming_Port_SequenceTx  {

	CloudUTF utf = new CloudUTF();
	ICloudWebDriver wd = null;
	CloudApplication capp;
	SaasUAUIImpl uiImpl= null;
	SaasUARestImpl restImpl = null;
	ILogger suiteLogger = utf.getLogger(); 
	UTFConfiguration config = new UTFConfiguration();
	

	
	
	ILogger logger = utf.getLogger();
	Utils utils = new Utils();
	ArrayList<String> connNames = new ArrayList<String>();
	
	public PDO_SnowflakeDW_Incoming_Port_SequenceTx()  {
		capp = new CloudApplication();
		capp.setBaseUrl(utf.getParameterByName("URL"));
		try {
			wd = utf.getCloudWebDriver(capp);
			uiImpl=(SaasUAUIImpl) utf.getSaasUIActionImpl();
			restImpl = (SaasUARestImpl) utf.getSaasRestActionImpl();
		} catch (CloudDriverException e) {
		
			e.printStackTrace();
		}

	}
	
   
	@BeforeMethod
	public void before()
	{
		/*{
		
		uiImpl.login(utf.getParameterByName("username"), utf.getParameterByName("password"));
		restImpl.loginv3(utf.getParameterByName("username"), utf.getParameterByName("password"));
		File folder = new File(System.getProperty("user.dir") + "\\data\\baseline\\mct_cmd\\\\Snowflake_ODBC_PDO\\Sanity\\zip");

		File[] files = folder.listFiles();

		for (File file : files) {
			System.out.println(file.getAbsolutePath());
			//restImpl.importPackage(file.getAbsolutePath());
			uiImpl.importTask_ui(file.getAbsolutePath(), utf.getParameterByName("agent"));
		}
		//restImpl.logoutv3();
		uiImpl.logout();
		}*/
	}
	
	/*@AfterMethod
	public void logout()
	{
		uiImpl.logout();
		restImpl.logout();
		
	}*/
	
	//@BeforeSuite
	public void Connection() 
	{
		{
		//restImpl.logoutAll(utf.getParameterByName("username"), utf.getParameterByName("password"));
		System.out.println("INSIDE BEFORE SUITE!!!");
		uiImpl.login(utf.getParameterByName("username"), utf.getParameterByName("password"));
		restImpl.loginv3(utf.getParameterByName("username"), utf.getParameterByName("password"));
		
		IConnectionMdl connMdl;
		ArrayList<ICloudModel> cloudTaskMdl=utf.loadDataModel("CONN_SnowflakeV2_SRC_JOIN.xml");
		ActionResult ar = new ActionResult();
		for(int i=0; i<cloudTaskMdl.size(); i++)
		{
			connMdl= (IConnectionMdl)cloudTaskMdl.get(i);
			ar = uiImpl.createConnection(connMdl);
			connNames.add(ar.getDataEntry("conn_name").toString());
		} 
		File folder = new File(System.getProperty("user.dir") + "\\data\\baseline\\mct_cmd\\Snowflake_EcoSystem_PDO\\Zip");

		File[] files = folder.listFiles();

		for (File file : files) {
			System.out.println(file.getAbsolutePath());
			restImpl.importPackage(file.getAbsolutePath());
		}
		
		restImpl.logoutv3();
		uiImpl.logout();	
		//copyFilesToS3(utf.getDataHome() + "\\" + "Sanity\\SourceFiles");
		}

}
	
	
	
	
	@Test(dataProvider="MCT_SNOWFLAKE_PDO_INCOMING_PORT_SEQUENCE.xml")
	public void Sanity(MCTDataModel mct, CloudContext ctx) throws IOException{
		suiteLogger.log("MCT MappingName: "+mct.getMctName());
		ILogger suiteLogger = utf.getLogger();
		suiteLogger.log("INSIDE Test!!!");	
		SaasUAUIImpl uiImpl = (SaasUAUIImpl) utf.getSaasUIActionImpl();
		restImpl.loginv3(utf.getParameterByName("username"), utf.getParameterByName("password"));
		suiteLogger.log("Model = "+mct.getMappingName());
		ActionResult ar = restImpl.runJob(mct.getMctName(), SERVICE_TYPE.MTT);
		restImpl.logoutv3();
		
		
		String SrcFile = utf.getParameterByName("SrcFilePath");
		String agentPlatform = utf.getParameterByName("AgentPlatform");
		String tgtPath=utf.getActualHome();
		config.setCompare(mct.isCompare());
		boolean flag=mct.isCompare();
		SerachStringLog searchlog=new SerachStringLog();
		String workflowLogsPath ="";
		String logfilename="";
		CMDModel cMdl = (CMDModel)utf.loadDataModel("Sanity\\CMD_SNOWFLAKE_PDO_INCOING_PORT_SEQUENCE.xml",mct.getMappingName());
		ArrayList<ITxMdl> cmdT = cMdl.getTransformations(TX_TYPE.TARGET); 
		String targetObject;// = ((TargetTxMdl) cmdT.get(0)).getTargeteObject();//Assuming the mapping contains 1 target
		//String srcPath = utf.getParameterByName("TgtFilePath")+"\\"+targetObject;
		String srcPath = utf.getParameterByName("TgtFilePath");
		String targetType;// = ((TargetTxMdl) cmdT.get(0)).getTargetTypeAbbr();
		String tgt_type;//=((TargetTxMdl) cmdT.get(0)).getTargetTypeAbbr();
		File src=new File(srcPath);
		if (flag == true) {
			for (int i = 0; i < cmdT.size(); i++) {

				targetObject = ((TargetTxMdl) cmdT.get(i)).getTargeteObject();
				tgt_type = ((TargetTxMdl) cmdT.get(i)).getTargetTypeAbbr();
				if (tgt_type.equals(CONNECTION_TYPE_ABBREVIATED.CSV.toString())) {
					System.out.println("Inside the write file loop....!!!!");

					String tgtPath1 = utf.getActualHome();

					System.out.println("Tgtpath= " + tgtPath1);
					File tgt = new File(tgtPath1);
					tgt.setExecutable(true);
					tgt.setReadable(true);
					tgt.setWritable(true);

					if (utf.getParameterByName("AgentPlatform").equalsIgnoreCase("linux")) {
						System.out.println("copying file from linux");

					} else if (utf.getParameterByName("AgentPlatform").equalsIgnoreCase("windows")) {
						System.out.println("copying file from windows");

					}
					com.infacloud.utils.FileUtils.filesCopy(agentPlatform, srcPath, tgtPath1, targetObject, utf);
				}
			}
		} else 
		{
			if(utf.getParameterByName("AgentPlatform").equalsIgnoreCase("windows")) 
			{
				workflowLogsPath = utf.getParameterByName("AgentInstallLocation") +
						File.separator + "apps" + File.separator + "Data_Integration_Server" +File.separator + "logs" + File.separator; 
				File logfile=searchlog.lastFileModified(workflowLogsPath);
				logfilename=logfile.getName(); suiteLogger.log("copying file from windows");
				com.infacloud.utils.FileUtils.filesCopy(agentPlatform, workflowLogsPath,tgtPath , logfilename, utf); 
			} else
				if(utf.getParameterByName("AgentPlatform").equalsIgnoreCase("linux")) 
				{
					workflowLogsPath = utf.getParameterByName("AgentInstallLocation") +"/"+"apps" + "/"+ "Data_Integration_Server" + "/" + "logs";
					suiteLogger.log("workflowLogsPath="+workflowLogsPath);
					logfilename=searchlog.getLatestModifiedFileFromLinuxAgent(workflowLogsPath,utf); suiteLogger.log("copying file from linux");
					com.infacloud.utils.FileUtils.filesCopy(agentPlatform ,workflowLogsPath,tgtPath ,logfilename, utf);
				} 
			String actuallogFile =utf.getActualHome().replace("\\.", "") + "\\" +logfilename;
			suiteLogger.log("actual File path**********"+actuallogFile); 
			String expectedFile=System.getProperty("user.dir")+"\\"+utf.getExpectedHome()+"\\" + "Pushdown" + "_" + ctx.getTestcaseId() + ".txt";
			//=======================================
			FileInputStream fis = new FileInputStream(actuallogFile); 
			  
		        
		        FileOutputStream fos = new FileOutputStream(expectedFile); 
		  
		        int b; 
		        while  ((b=fis.read()) != -1) 
		            fos.write(b); 
		  
		        
		        fis.close(); 
		        fos.close(); 
		        
		      // =====================================
		        
			suiteLogger.log("expected File path**********"+expectedFile); 
			boolean	optQueryGenrtd=searchlog.IsMessageContainedInFile(actuallogFile,expectedFile,utf); 
			if(optQueryGenrtd) 
			{ 
				suiteLogger.log("*******************Optimized Query Found********************"); 
			}

			else {

				suiteLogger.log("*********************Optimized Query not Found***************");
				//Make test to fail as Optimized query is not found in log file
				Assert.assertTrue(false);
			}
		}


		// uiImpl.deleteTask(SERVICE_TYPE.CI, mctName);
		// uiImpl.deleteTask(SERVICE_TYPE.CI, mName);

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	
	//@AfterSuite
	public void tearDown() throws ParseException, NotSupportedException
	{
		uiImpl.login(utf.getParameterByName("username"), utf.getParameterByName("password"));
		uiImpl.deleteConnection(connNames);
		uiImpl.logout();
		restImpl.logoutAll(utf.getParameterByName("username"), utf.getParameterByName("password"));
		
		//Deleting all the Source files from Amazon S3
		//deleteFilesFromS3(utf.getDataHome() + "\\" + "TABLEAU_DSS\\SourceFiles");
		//deleteFilesFromS3(utf.getDataHome() + "\\" + "Sanity\\\\SourceFiles");
	}	
	
}
