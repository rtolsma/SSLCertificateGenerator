package serverCommunications;

import clientFeatures.MyException;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Date;


/**
 * Created by ryan on 12/4/14.
 */

public class CreateCertificates {
    KeyPairGenerator kGen;
    KeyPair keyPair;
    X509V3CertificateGenerator certGen;
    String domainName, pass;
    X509Certificate cert;
    File certificate, ksFile ;
    KeyStore privateKS=null;
    public CreateCertificates(String domainName, String password) {
      Security.addProvider(new BouncyCastleProvider());
        pass=password;
        this.domainName=domainName;
        try {
          kGen=KeyPairGenerator.getInstance("RSA");

        kGen.initialize(2048);
        keyPair=kGen.genKeyPair();
          certGen=new X509V3CertificateGenerator();
          certGen.setSerialNumber(BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())));
          certGen.setIssuerDN(new X509Principal("CN=" + domainName + ", OU=None, O=None L=None, C=None"));
          certGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
          certGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)));
          certGen.setSubjectDN(new X509Principal("CN=" + domainName + ", OU=None, O=None L=None, C=None"));
          certGen.setPublicKey(keyPair.getPublic());
          certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
         cert =certGen.generateX509Certificate(keyPair.getPrivate());
      } catch (NoSuchAlgorithmException e) {
          new Thread(new MyException(e, "NoSuchAlgorithmException: Undefined algorithm in creating ssl certificates")).start();
      } catch (InvalidKeyException e) {
          new Thread(new MyException(e, "InvalidKeyException: in creating the ssl certificate")).start();
      } catch(SignatureException e) {
          new Thread(new MyException(e,"SignatureException: cant generate certificate")).start();

      }
    }

            public  File createCertificates(String path, String ksPath) {
                try {
                    File file=new File(path);
                  //  if(!file.exists()) {
                    FileOutputStream wr=new FileOutputStream((file));
                    System.out.println("created certificate");
                    wr.write(cert.getEncoded());
                    wr.close(); // }
                    certificate=new File(file.getPath());
                    privateKS=KeyStore.getInstance(KeyStore.getDefaultType());
                    privateKS.load(null,pass.toCharArray());
                    privateKS.setKeyEntry("John Doe", keyPair.getPrivate(), pass.toCharArray(), new Certificate[] {cert});
                    privateKS.store(new FileOutputStream(ksFile=new File(ksPath)), pass.toCharArray());
                    return file;
                } catch(FileNotFoundException e) {
                    new Thread(new MyException(e,"FileNotFoundException: cant create certificate")).start();
                } catch(CertificateEncodingException e) {
                    new Thread(new MyException(e,"CertificateEncodingException: cant create certificate")).start();
                } catch(IOException e) {
                    new Thread(new MyException(e,"IOException: error in creating the certificate")).start();
                } catch (Exception e) {
                    new Thread(new MyException(e, "Exception: Error in creating the certificates")).start();
                }
                return null;
            }
                public String getPassword() {
                    return pass;
                }
                    public File getKeyStore() {
                        return  ksFile; }
    public File getCertificate() {
        return  certificate;
    }

}
