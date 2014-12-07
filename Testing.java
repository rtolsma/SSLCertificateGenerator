
import serverCommunications.CreateCertificates;
import serverCommunications.hostSocketServer;

import java.io.File;


/**
 * Created by ryan on 11/10/14.
 */
public class TestingServer {
    public static void main(String[] args)  {
   
		String folderpath="/home/ryan"
            String pass="happy";
         CreateCertificates c=new CreateCertificates("127.0.0.1", "pass");
        c.createCertificates(folderpath+"/javaCert.cert", folderpath+"/javaKeystore.jks");
       new Thread(new hostSocketServer(pass, 1234, c)).start();


      try{
          Thread.sleep(5000);}
      catch(InterruptedException e) {
          e.printStackTrace();
         }   new Thread(new setupConnection(null,pass, 1234, 0, c)).start();

    }


    }



