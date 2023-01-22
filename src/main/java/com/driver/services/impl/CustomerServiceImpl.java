package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		customerRepository2.deleteById(customerId);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		try{
			Customer tripcustomer = customerRepository2.findById(customerId).get();
			Driver tripDriver = null;

			List<Driver> driverList = driverRepository2.findAll();
			for(Driver driver: driverList){
				if(driver.getCab().isAvailable()==true){
					tripDriver = driver; break;
				}
			}

			if(tripDriver==null){
				throw new Exception("No cab available!");
			}

			TripBooking nextTrip = new TripBooking();

			nextTrip.setFromLocation(fromLocation);
			nextTrip.setToLocation(toLocation);
			nextTrip.setDistanceInKm(distanceInKm);
			nextTrip.setStatus(TripStatus.CONFIRMED);
			nextTrip.setBill(tripDriver.getCab().getPerKmRate()*distanceInKm);
			nextTrip.setDriver(tripDriver);
			nextTrip.setCustomer(tripcustomer);

			tripcustomer.getTripBookingList().add(nextTrip);
			tripDriver.getTripBookingList().add(nextTrip);
			tripDriver.getCab().setAvailable(false);

			tripBookingRepository2.save(nextTrip);

			List<TripBooking> tripsByCustomer = customerRepository2.findById(customerId).get().getTripBookingList();
			int tripsBooked = tripsByCustomer.size();

			return tripsByCustomer.get(tripsBooked-1);
		}
		catch(Exception e){
			throw e;
		}
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking trip = tripBookingRepository2.findById(tripId).get();

		trip.setStatus(TripStatus.CANCELED);
		trip.getDriver().getCab().setAvailable(true);

		tripBookingRepository2.save(trip);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking trip = tripBookingRepository2.findById(tripId).get();

		trip.setStatus(TripStatus.COMPLETED);
		trip.getDriver().getCab().setAvailable(true);

		tripBookingRepository2.save(trip);
	}
}
