/**
 * The cashier is responsible to reply pay action and deliver an available pump to customer
 * 
 * @author RenDing
 * studentID u5111810
 */

public class Cashier {
	/** the cashier belong to a gas station*/
	private GasStation gasStation;
	/**gas station event logger */
	private GasStationEvent gasStationEvent;
	
	/**
	 * Cashier constructor
	 * @param gasStationEvent gas station event logger
	 */
	public Cashier(GasStationEvent gasStationEvent) {
		this.gasStationEvent = gasStationEvent;
	}
	
	/**
	 * connecting the gas station, which the cashier is belong to
	 * @param gasStation
	 */
	public void belongToAGasStation(GasStation gasStation) {
		this.gasStation = gasStation;
	}
	
	
	/**
	 * the pay action will check if there is an available pump, and then delivering a pump to standard customer
	 * if all pumps are in use, wait the thread
	 * 
	 * @param customerID				customer ID
	 * @param howManyUnits				the units of gas the customer will pay
	 * @throws InterruptedException
	 */
	public synchronized int pay(int customerID, int howManyUnits) throws InterruptedException {
		//delivering a pump to the customer
		this.gasStation.standardCounterIncreasement();
		
		for(int i = 0; i < this.gasStation.getNumOfPumps(); i++) {
			if(!this.gasStation.getPumps()[i].isInUsed()) {
				
				this.gasStation.getPumps()[i].inUsed();
				
				this.gasStation.setStandardPetrol(true);
				this.gasStationEvent.logPay(customerID, howManyUnits);
				return i;
			}
		}
		
		return -1;// error happens
	}
	
	/**
	 * the pay action will check if there is an available pump, and then delivering a pump to E10 customer
	 * if all pumps are in use, wait the thread
	 * 
	 * @param customerID				customer ID
	 * @param howManyUnits				the units of gas the customer will pay
	 * @throws InterruptedException
	 */
	public synchronized int payETen(int customerID, int howManyUnits) throws InterruptedException {
		//delivering a pump to the customer
		this.gasStation.eTenCounterIncreasement();
		
		for(int i = 0; i < this.gasStation.getNumOfPumps(); i++) {
			if(!this.gasStation.getPumps()[i].isInUsed()) {
				
				this.gasStation.getPumps()[i].inUsed();
				
				this.gasStation.setStandardPetrol(false);
				this.gasStationEvent.logPay(customerID, howManyUnits);
				return i;
			}
		}
		
		return -1;// error happens
	}

}
