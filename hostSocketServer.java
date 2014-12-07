package serverCommunications;

import clientFeatures.MyException;

import javax.net.ssl.*;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Created by ryan on 11/30/14.
 *
 * Conncects to setupConnection client, and sends commands that are designated by the gui
 *
 * indicated port must be port forwarded
 *
 * ???May have to use Data streams
 */
public class hostSocketServer implements Runnable  {

    int port;
    SSLServerSocket serverSocket;
    SSLSocket connection;
    int timeout=0;  //never timeout/infinite timeout

     private PrintWriter wr;
   private  BufferedReader br, keyStoreReader;
boolean listening;
    String certificatePassword, password, temp;
    String pickedCipher[] ={"TLS_RSA_WITH_AES_256_CBC_SHA"};
    KeyStore ks;
    KeyManagerFactory kmf;
    SSLContext ctx;
    File keyStore;
    public hostSocketServer(String password, int port, CreateCertificates c) {
            this.password=password;
        certificatePassword=c.getPassword();
        this.port=port;
            this.ks=ks;
                listening=true;
                try {
                    keyStoreReader=new BufferedReader(new FileReader(keyStore=c.getCertificate()));
                    kmf=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    ks=KeyStore.getInstance(KeyStore.getDefaultType());
                    ks.load(new FileInputStream(c.getKeyStore()), certificatePassword.toCharArray());
                    kmf.init(ks, certificatePassword.toCharArray());
                    ctx=SSLContext.getInstance("SSL");
                    ctx.init(kmf.getKeyManagers(), null, null);
                 //   SSLServerSocketFactory sslSrvFact= (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                   SSLServerSocketFactory sslSrvFact= ctx.getServerSocketFactory();
                    serverSocket=(SSLServerSocket) sslSrvFact.createServerSocket(port);
                  //  serverSocket = new ServerSocket(port);
                    serverSocket.setSoTimeout(timeout);
                    serverSocket.setNeedClientAuth(false);

                   // connection=new Socket();
                } catch (IOException e) {
                    new Thread(new MyException(e, "IOException in creating keystore or in creating a ServerSocket bound to port "+port)).start();
                }catch (Exception e) {
                    new Thread(new MyException(e, "Exception: Error in ssl and keystore initialization")).start();
                }
            }


        public void run() {

                try {
                    int x=0;
                    System.out.println("\n*****Starting Listening*****");

                    while(listening) {
                        serverSocket.setEnabledCipherSuites(pickedCipher);
                      //  connection=(SSLSocket) serverSocket.accept();
                       // connection.setEnabledCipherSuites(pickedCipher);
                  new Thread(new serverConnectionThread(password, (connection=(SSLSocket) serverSocket.accept()))).start();
                /*        System.out.println("This is the "+(++x)+" connection");
                      // connection = serverSocket.accept();
                        //  br = new DataInputStream(connection.getInputStream());
                        //wr = new DataOutputStream(connection.getOutputStream());
                        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        wr = new PrintWriter(connection.getOutputStream(), true);
                        //temp=br.readUTF();
                        temp = br.readLine();
                        System.out.println(temp + " client message");
                        wr.println("this is a test server message");
                        //     wr.writeUTF("This is a test server message\n");  */

                    }
                  } catch (BindException e) {
                    new Thread(new MyException(e, "BindException: Server unable to bind to port number " + port)).start();
                } catch(SSLException e) {
                    new Thread(new MyException(e, "SSLException: Server and client had ssl error in establishing connection")).start();
                    } catch (IOException e){
                    new Thread(new MyException(e, "socket server io exception")).start();
                } catch(Exception e) {
                    new Thread(new MyException(e , "Socket Server exception")).start();
                }
            }

        }



