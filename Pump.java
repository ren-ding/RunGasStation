/**
 * the pump is responsible to start and gas to the customer's car.
 * 
 * @author RenDing
 * studentID u5111810
 */

public class Pump {
	/** pump id */
	private int pumpID;
	/** gas event logger */
	private GasStationEvent gasStationEvent;
	/** the flag indicate if the pump is in use or not */
	private boolean inUse;
	
	/**
	 * Pump constructor
	 * 
	 * @param pumpID				pump id 
	 * @param gasStationEvent		gas event logger
	 */
	public Pump(int pumpID, GasStationEvent gasStationEvent) {
		this.pumpID = pumpID;
		this.gasStationEvent = gasStationEvent;
		this.inUse = false;
	}
	
	/**
	 * start to gas for standard customer, turn the pump state to "inUse"
	 * 
	 * @param customerID			the customer who paid for the pump
	 * @param howManyUnits			the units of gas the customer paid
	 */
	public synchronized void start(int customerID, int howManyUnits) {
		this.gasStationEvent.logStart(customerID, howManyUnits, this.pumpID);
	}
	
	/**
	 * finish gassing for standard customer, turn the pump state to "not inUse"
	 * 
	 * @param customerID			the customer who paid for the pump
	 * @param howManyUnits			the units of gas the customer paid
	 */
	public synchronized void gas(int customerID, int howManyUnits, GasStation gasStation) throws InterruptedException{
		this.gasStationEvent.logGas(customerID, howManyUnits, this.pumpID);
		inUse = false;
		
		gasStation.standardCounterDecreasement();
	}
	
	/**
	 * start to gas for E10 customer, turn the pump state to "inUse"
	 * 
	 * @param customerID			the customer who paid for the pump
	 * @param howManyUnits			the units of gas the customer paid
	 */
	public synchronized void startETen(int customerID, int howManyUnits) {
		this.gasStationEvent.logStart(customerID, howManyUnits, this.pumpID);
	}
	
	/**
	 * finish gassing for E10 customer, turn the pump state to "not inUse"
	 * 
	 * @param customerID			the customer who paid for the pump
	 * @param howManyUnits			the units of gas the customer paid
	 */
	public synchronized void gasETen(int customerID, int howManyUnits, GasStation gasStation) throws InterruptedException{
		this.gasStationEvent.logGas(customerID, howManyUnits, this.pumpID);
		inUse = false;
		
		gasStation.eTenCounterDecreasement();
	}
	
	/**
	 * set the pump's state to in used
	 */
	public void inUsed() {
		this.inUse = true;
	}
	
	/**
	 * return if the pump is in use or not
	 * @return if the pump is in use or not
	 */
	public boolean isInUsed() {
		return this.inUse;
	}
}
