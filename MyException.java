	

	public class MyException extends Exception {
	Exception exception;
	String message;
 	
	public MyException(Exception e, String message) {
		e.printStackTrace();
		System.err.println((this.message=message));
           }
