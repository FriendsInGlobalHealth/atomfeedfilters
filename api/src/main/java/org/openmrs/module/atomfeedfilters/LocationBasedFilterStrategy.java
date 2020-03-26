package org.openmrs.module.atomfeedfilters;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.OpenmrsObject;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.filter.FeedFilterStrategy;
import org.openmrs.module.atomfeed.api.filter.GenericFeedFilterStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBException;

import static org.openmrs.module.atomfeedfilters.Constants.ANY_VALUE;
import static org.openmrs.module.atomfeedfilters.Constants.DEFAULT_LOCATION_FIELDS;
import static org.openmrs.module.atomfeedfilters.Constants.FIELD_DELIMITER;
import static org.openmrs.module.atomfeedfilters.Constants.GP_LOCATION_FIELDS;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 10/23/19.
 */
@Component("atomfeedFilters.LocationBasedFilterStrategy")
public class LocationBasedFilterStrategy extends FeedFilterStrategy implements GenericFeedFilterStrategy {
	
	@Autowired
	private AdministrationService adminService;
	
	@Override
	protected String getBeanName() {
		return "atomfeedFilters.LocationBasedFilterStrategy";
	}
	
	@Override
	public String createFilterFeed(OpenmrsObject openmrsObject) {
		String filter = null;
		final String LOCATION_FIELDS = adminService.getGlobalProperty(GP_LOCATION_FIELDS, DEFAULT_LOCATION_FIELDS);
		if (openmrsObject instanceof Encounter) {
			Encounter encounter = (Encounter) openmrsObject;
			Location location = encounter.getLocation();
			filter = location == null ? null : createLocationFilter(location, LOCATION_FIELDS);
		} else if (openmrsObject instanceof Visit) {
			Visit visit = (Visit) openmrsObject;
			Location location = visit.getLocation();
			filter = location == null ? null : createLocationFilter(location, LOCATION_FIELDS);
		} else if (openmrsObject instanceof Obs) {
			Obs obs = (Obs) openmrsObject;
			Location location = obs.getLocation();
			if (location != null) {
				filter = createLocationFilter(location, LOCATION_FIELDS);
			} else if (obs.getEncounter() != null) {
				location = obs.getEncounter().getLocation();
				filter = location == null ? null : createLocationFilter(location, LOCATION_FIELDS);
			}
		}
		return createFeedFilterXML(filter);
	}
	
	@Override
	public boolean isFilterTagValid(String filter) {
		if (filter == null)
			return false;
		
		String comparisonValue = adminService.getGlobalProperty(Constants.GP_LOCATION_FILTERING_VALUE, ANY_VALUE);
		if ("".equals(comparisonValue) || ANY_VALUE.equals(comparisonValue)) {
			return true;
		}
		
		String[] filterValues = filter.split(FIELD_DELIMITER);
		String[] filterPropertyValues = comparisonValue.split(FIELD_DELIMITER);
		
		if (filterValues.length != filterPropertyValues.length) {
			return false;
		}
		
		for (int i = 0; i < filterValues.length; i++) {
			if (filterValueDoesNotMatch(filterPropertyValues[i], filterValues[i])) {
				return false;
			}
		}
		return true;
	}
	
	private String createFeedFilterXML(String filter) {
		if (filter == null) {
			return null;
		}
		FeedFilter feedFilter = new FeedFilter(getBeanName(), filter);
		
		String xml;
		try {
			xml = getXmlParseService().createXMLFromFeedFilter(feedFilter);
		}
		catch (JAXBException e) {
			throw new AtomfeedException(e);
		}
		return xml;
	}
	
	private static String createLocationFilter(Location location, String addressFields) {
		Assert.notNull(location);
		Assert.hasText(addressFields);
		String[] fields = addressFields.split(FIELD_DELIMITER);
		StringBuilder sb = new StringBuilder();
		for (String addressField : fields) {
			switch (addressField) {
				case "name":
					sb.append(location.getName()).append(FIELD_DELIMITER);
					break;
				case "address1":
					sb.append(location.getAddress1()).append(FIELD_DELIMITER);
					break;
				case "address2":
					sb.append(location.getAddress2()).append(FIELD_DELIMITER);
					break;
				case "address3":
					sb.append(location.getAddress3()).append(FIELD_DELIMITER);
					break;
				case "address4":
					sb.append(location.getAddress4()).append(FIELD_DELIMITER);
					break;
				case "address5":
					sb.append(location.getAddress5()).append(FIELD_DELIMITER);
					break;
				case "address6":
					sb.append(location.getAddress6()).append(FIELD_DELIMITER);
					break;
				case "country":
					sb.append(location.getCountry()).append(FIELD_DELIMITER);
					break;
				case "state_province":
					sb.append(location.getStateProvince()).append(FIELD_DELIMITER);
					break;
				case "county_district":
					sb.append(location.getCountyDistrict()).append(FIELD_DELIMITER);
					break;
				case "city_village":
					sb.append(location.getCityVillage()).append(FIELD_DELIMITER);
					break;
				case "postal_code":
					sb.append(location.getPostalCode()).append(FIELD_DELIMITER);
					break;
				case "longitude":
					sb.append(location.getLongitude()).append(FIELD_DELIMITER);
					break;
				case "latitude":
					sb.append(location.getLatitude()).append(FIELD_DELIMITER);
					break;
				default:
					sb.append("");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	private static boolean filterValueDoesNotMatch(String filterPropertyValue, String filterValue) {
		return !ANY_VALUE.equals(filterPropertyValue) && !filterPropertyValue.equalsIgnoreCase(filterValue);
	}
}
