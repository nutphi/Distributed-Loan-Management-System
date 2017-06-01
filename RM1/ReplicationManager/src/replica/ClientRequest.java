package replica;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Calendar;

public class ClientRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int sequence;
	
	/** Sequencer will add the InetSocketAddress from which it had received the message. 
	 * So, that replicas can send the result on the same InetAddress. 
	 */
	
	private InetSocketAddress clientRequestAddress;

	private int methodName;

	//0 openAccount

	//1 getLoan

	//2 transferLoan

	//3 delayPayment
	
	//4 PrintCustomer Info

	private String _bank;

	private String _firstName;

	private String _lastName;

	private String _emailAddress;

	private String _phoneNumber;

	private String _password;

	private String _accountNumber;

	private int _loanAmount;

	private String _loanID;

	private String _CurrentBank;

	private String _OtherBank;

	private String currentDate;

	private String newDuedate;

	/**

	*

	* Constructor for openAccount

	*/

	public ClientRequest(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {

	// TODO Auto-generated constructor stub

	_bank = bank;

	_firstName = firstName;

	_lastName = lastName;

	_emailAddress = emailAddress;

	_password = password;

	_phoneNumber = phoneNumber;

	methodName=0;

	}

	/**

	* Constructor for getLoan

	* @param seq

	*/

	public ClientRequest(String bank, String accNum, String password, int loanAmt) {

	// TODO Auto-generated constructor stub

	_bank = bank;

	_password = password;

	_accountNumber = accNum;

	_loanAmount = loanAmt;

	methodName=1;

	}

	/**

	* Constructor for transferLoan

	*/

	public ClientRequest(String loanID, String currBank, String othBank) {

	_loanID=loanID;

	_CurrentBank=currBank;

	_OtherBank=othBank;

	methodName=2;

	}
	
	/**
	 * Constructor for Delay Payment
	 * @return
	 */
	public ClientRequest(String bank, String loanID, String currDate, String newDate){
	
		_bank = bank;
		_loanID = loanID;
		currentDate = currDate;
		newDuedate = newDate;
		methodName =  3;  
	}
	
	/**
	 * Constructor for Print Customer Info
	 * @return
	 */
	public ClientRequest(String bank){
		_bank =  bank;
		methodName = 4;
	}

	public int getSequence() {

	return sequence;

	}

	public void setSequence(int sequence) {

	this.sequence = sequence;

	}
	
	public void setClientRequestAddress(InetSocketAddress clntReqAdd){
		clientRequestAddress = clntReqAdd;
	}
	
	public InetSocketAddress getClientRequestAddress(){
		return clientRequestAddress;
	}

	public int getMethodName() {

	return methodName;

	}

	public void setMethodName(int methodName) {

	this.methodName = methodName;

	}

	public String get_bank() {

	return _bank;

	}

	public void set_bank(String _bank) {

	this._bank = _bank;

	}

	public String get_firstName() {

	return _firstName;

	}

	public void set_firstName(String _firstName) {

	this._firstName = _firstName;

	}

	public String get_lastName() {

	return _lastName;

	}

	public void set_lastName(String _lastName) {

	this._lastName = _lastName;

	}

	public String get_emailAddress() {

	return _emailAddress;

	}

	public void set_emailAddress(String _emailAddress) {

	this._emailAddress = _emailAddress;

	}

	public String get_phoneNumber() {

	return _phoneNumber;

	}

	public void set_phoneNumber(String _phoneNumber) {

	this._phoneNumber = _phoneNumber;

	}

	public String get_password() {

	return _password;

	}

	public void set_password(String _password) {

	this._password = _password;

	}

	public String get_accountNumber() {

	return _accountNumber;

	}

	public void set_accountNumber(String _accountNumber) {

	this._accountNumber = _accountNumber;

	}

	public int get_loanAmount() {

	return _loanAmount;

	}

	public void set_loanAmount(int _loanAmount) {

	this._loanAmount = _loanAmount;

	}

	public String get_loanID() {

	return _loanID;

	}

	public void set_loanID(String _loanID) {

	this._loanID = _loanID;

	}

	public String get_CurrentBank() {

	return _CurrentBank;

	}

	public void set_CurrentBank(String _CurrentBank) {

	this._CurrentBank = _CurrentBank;

	}

	public String get_OtherBank() {

	return _OtherBank;

	}

	public void set_OtherBank(String _OtherBank) {

	this._OtherBank = _OtherBank;

	}

	public String getCurrentDate() {

	return currentDate;

	}

	public void setCurrentDate(String currentDate) {

	this.currentDate = currentDate;

	}

	public String getNewDuedate() {

	return newDuedate;

	}

	public void setNewDuedate(String newDuedate) {

	this.newDuedate = newDuedate;

	}

	}


