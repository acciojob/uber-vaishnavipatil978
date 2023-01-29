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
		Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception {
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

		try {
			Driver tripDriver = null;

			int min = Integer.MAX_VALUE;

			List<Driver> driverList = driverRepository2.findAll();
			for (Driver driver : driverList) {
				if (driver.getCab().getAvailable() == true && driver.getDriverId() < min) {
					tripDriver = driver;
					min = driver.getDriverId();
				}
			}

			if (tripDriver == null || min == Integer.MAX_VALUE) {
				throw new Exception("No cab available!");
			}

			Customer tripcustomer = customerRepository2.findById(customerId).get();

			TripBooking nextTrip = new TripBooking();

			nextTrip.setFromLocation(fromLocation);
			nextTrip.setToLocation(toLocation);
			nextTrip.setDistanceInKm(distanceInKm);
			nextTrip.setStatus(TripStatus.CONFIRMED);
			nextTrip.setBill(tripDriver.getCab().getPerKmRate() * distanceInKm);
			nextTrip.setDriver(tripDriver);
			nextTrip.setCustomer(tripcustomer);

			tripcustomer.getTripBookingList().add(nextTrip);
			tripDriver.getTripBookingList().add(nextTrip);
			tripDriver.getCab().setAvailable(false);

			tripBookingRepository2.save(nextTrip);
			customerRepository2.save(tripcustomer);
			driverRepository2.save(tripDriver);

			return nextTrip;
		}
		catch (Exception e){
			return new TripBooking();
		}

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking trip = tripBookingRepository2.findById(tripId).get();

		trip.setStatus(TripStatus.CANCELED);
		trip.setBill(0);
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
