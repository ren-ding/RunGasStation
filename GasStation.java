/**
 * It contains a cashier, few pumps and four counters(count for the number of pumps in use, the monitors).
 * 
 * The cashier is responsible to reply pay action and deliver an available pump to customer,
 * the delivered pump is responsible to start and gas to the customer's car,
 * the counter as a monitor records the number of pumps in use.
 * 
 * @author RenDing
 * studentID u5111810
 */


public class GasStation {
	/** cashier */
	private Cashier cashier;
	/** pumps */
	private Pump[] pumps;
	/** the number of pumps*/
	private int numOfPumps;
	/** gas type */
	private boolean standardPetrol;
	
	/**the number of pumps in use for standard customer*/
	private int standardCounter;
	/**the number of pumps in use for E10 customer*/
	private int eTenCounter;
	
	/** waitStandardCounter, for extended system part2, fair system*/
	private int waitStandardCounter;
	/** waitETenCounter, for extended system part2, fair system*/
	private int waitETenCounter;
	
	/**
	 * GasStation constructor
	 * 
	 * @param cashier the cashier in the gas station
	 * @param pumps	  all the pumps in the gas station
	 */
	public GasStation(Cashier cashier, Pump[] pumps) {
		this.cashier = cashier;
		this.pumps = pumps;
		
		this.numOfPumps = pumps.length;
		
		for(int i = 0;i < this.numOfPumps;i++) {
			this.pumps[i] = pumps[i];
		}
		
		//initially, no pump is in use,max size = numOfPumps
		this.standardCounter = 0;
		this.eTenCounter = 0;
		this.waitStandardCounter = 0;
		this.waitETenCounter = 0;
		
		//the default gas type is standard
		standardPetrol = true;
	}
	
	/**
	 * return cashier
	 * @return the cashier
	 */
	public Cashier getCashier() {
		return this.cashier;
	}
	
	/**
	 * return pumps
	 * @return the pumps array
	 */
	public Pump[] getPumps() {
		return this.pumps;
	}

	/**
	 * return the number of pumps
	 * @return the number of pumps
	 */
	public int getNumOfPumps() {
		return this.numOfPumps;
	}
	
	/**
	 * increasing the standard counter by 1, shall be synchronized.
	 * When standardCounter greater than or equal to the number of pumps(no available pumps), make the customer wait
	 * When standardCounter not equal to 0 (some pump is in use) and the gas type is different, make the customer wait
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void standardCounterIncreasement() throws InterruptedException {
		//An internal monitor invariant checking method
		assert(standardCounter >=0 && standardCounter <= numOfPumps);
		
		waitStandardCounter++;
		if( (this.standardCounter + this.eTenCounter >= this.numOfPumps) ||
			(this.standardCounter != 0 && !this.standardPetrol) ||
			(waitETenCounter>0 && !isStandardPetrol() ) ) wait();
		waitStandardCounter--;
		
		this.standardPetrol = true;
	
		this.standardCounter++;
		notifyAll();
	}
	
	/**
	 * decreasing the standardCounter by 1, wake up all customers
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void standardCounterDecreasement() throws InterruptedException {
		//An internal monitor invariant checking method
		assert(standardCounter >=0 && standardCounter <= numOfPumps);
		
		if(this.standardCounter == 0) wait();
		this.standardCounter--;
		notifyAll();
	}
	
	
	/**
	 * increasing the E10 counter by 1, shall be synchronized.
	 * When standardCounter greater than or equal to the number of pumps(no available pumps), make the customer wait
	 * When standardCounter not equal to 0 (some pump is in use) and the gas type is different, make the customer wait
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void eTenCounterIncreasement() throws InterruptedException {
		//An internal monitor invariant checking method
		assert(eTenCounter >=0 && eTenCounter <= numOfPumps);
		
		waitETenCounter++;
		if( (this.standardCounter + this.eTenCounter >= this.numOfPumps) ||
			(this.eTenCounter != 0 && this.standardPetrol) ||
			(waitStandardCounter>0 && isStandardPetrol()) ) wait();
		waitETenCounter--;
		
		this.standardPetrol = false;
	
		this.eTenCounter++;
		notifyAll();
	}
	
	/**
	 * decreasing the E10 Counter by 1, wake up all customers
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void eTenCounterDecreasement() throws InterruptedException {
		//An internal monitor invariant checking method
		assert(eTenCounter >=0 && eTenCounter <= numOfPumps);
		
		if(this.eTenCounter == 0) wait();
		this.eTenCounter--;
		notifyAll();
	}
	
	/**
	 * return if pumps offer standard gas or not
	 * @return	if pumps offer standard gas or not
	 */
	public boolean isStandardPetrol() {
		return this.standardPetrol;
	}
	
	/**
	 * set the gas type, which pumps offered
	 * @param standardPetrol the gas type will be settled
	 */
	public void setStandardPetrol(boolean standardPetrol) {
		this.standardPetrol = standardPetrol;
	}
}
