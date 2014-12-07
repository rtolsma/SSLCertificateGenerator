package clientCommunications;

import clientFeatures.MyException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import serverCommunications.CreateCertificates;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.Security;

/**
 * Created by ryan on 11/30/14.
 * Created with specified port, host, timeout
 * listens for feedback from server for command, and executes a new thread of the specified process/feature
 * a NULL host string will result in connecting to localhost
 */
public class setupConnection implements Runnable{

    String host, temp, certifcatePassword, password;
    int port, timeout;
    URL hostURL;
    SSLSocket connection;
    //Socket connection;
    // DataInputStream br;
    //DataOutputStream wr;
 private   PrintWriter wr;
   private BufferedReader br, stdin, ioKeystore;
    private OutputStream os;
    private InputStream is ;
    String pickedCipher[] ={"TLS_RSA_WITH_AES_256_CBC_SHA"};
    KeyStore ts;
    TrustManagerFactory tmf;
    SSLContext sslContext;
    File trustStore;
    public setupConnection(String host, String password, int port, int timeout, CreateCertificates c) {
            this.password=password;
            this.certifcatePassword=c.getPassword();
            this.host=host;
            this.port=port;
            this.timeout=timeout;
            try {
                hostURL = new URL("http://"+host);
             //   connection=new Socket(host, port);

                ioKeystore= new BufferedReader(new FileReader(trustStore=c.getCertificate()));
                ts=KeyStore.getInstance(KeyStore.getDefaultType());
                ts.load(new FileInputStream(c.getKeyStore()), certifcatePassword.toCharArray());
                tmf=TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ts);
                sslContext=SSLContext.getInstance("SSL");
                sslContext.init(null, tmf.getTrustManagers(), null);
                SSLSocketFactory sslFact= (SSLSocketFactory) sslContext.getSocketFactory();
                connection=(SSLSocket) sslFact.createSocket(host, port);
                Security.addProvider(
                        new com.sun.net.ssl.internal.ssl.Provider());
            } catch(MalformedURLException e) {
         new Thread(new MyException(e, "MalformedURLException: Server host url is malformed, or doesn't exist")).start();
            } catch (UnknownHostException e) {
                new Thread(new MyException(e, "UnknownHostException: Client connection socket can't connect")).start();
            } catch (IOException e) {
                new Thread(new MyException(e, "IOException: client socket has io error")).start();
            } catch (Exception e) {
                new Thread(new MyException(e, "Exception: Error in ssl and keystore initialization")).start();
            }

        }

    public void run() {
    byte[] buf;
            try {

                   if(connection==null) {
                       SSLSocketFactory sslFact= (SSLSocketFactory) SSLSocketFactory.getDefault();
                       connection=(SSLSocket) sslFact.createSocket(host, port);
                   }
                connection.setEnabledCipherSuites(pickedCipher);
                is = connection.getInputStream();
                os=connection.getOutputStream();
                wr=new PrintWriter(os, true);
               br=new BufferedReader(new InputStreamReader(is));
             //   stdin=new BufferedReader(new InputStreamReader(System.in));
               // connection.setKeepAlive(true);
                System.out.println("setup data streams");
                wr.println(password);
                temp=br.readLine();
                    System.out.println(temp+" Server message");
              //  System.out.println((temp=stdin.readLine())+"Server message stdin");
              //  System.out.println((temp=stdin.readLine())+"Server message stdin");

            wr.close();
                br.close();
          //      if(stdin.ready())
            //    stdin.close();

   //            }

            } catch (IOException e) {
                new Thread(new MyException(e, "Exception in creating io streams to server")).start();
            }

        }
    }


