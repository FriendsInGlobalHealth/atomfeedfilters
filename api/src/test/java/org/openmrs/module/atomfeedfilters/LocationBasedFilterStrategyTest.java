package org.openmrs.module.atomfeedfilters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.XMLParseService;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.atomfeedfilters.Constants.DEFAULT_LOCATION_FIELDS;
import static org.openmrs.module.atomfeedfilters.Constants.FIELD_DELIMITER;
import static org.openmrs.module.atomfeedfilters.Constants.GP_LOCATION_FIELDS;
import static org.openmrs.module.atomfeedfilters.Constants.GP_LOCATION_FILTERING_VALUE;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 10/23/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationBasedFilterStrategyTest {
	
	@Mock
	private AdministrationService adminService;
	
	@Mock
	private XMLParseService xmlParseService;
	
	@InjectMocks
	private LocationBasedFilterStrategy locationBasedFilterStrategy = new LocationBasedFilterStrategy();
	
	@Test
	public void isTagValidShouldReturnTrueWhenTagIsValid() {
		final String TEST_LOCATION_VALUE = "Quelimane%Coalane";
		Mockito.when(adminService.getGlobalProperty(GP_LOCATION_FILTERING_VALUE, Constants.ANY_VALUE)).thenReturn(
		    TEST_LOCATION_VALUE);
		assertTrue(locationBasedFilterStrategy.isFilterTagValid(TEST_LOCATION_VALUE));
		assertTrue(locationBasedFilterStrategy.isFilterTagValid(TEST_LOCATION_VALUE.toUpperCase()));
		assertFalse(locationBasedFilterStrategy.isFilterTagValid(null));
		assertFalse(locationBasedFilterStrategy.isFilterTagValid("not-same-as-set"));
	}
	
	@Test
	public void isTagValidShouldReturnTrueWhenMatchedAgainstAnyValueInIndividualFields() {
		Mockito.reset(adminService);
		Mockito.when(adminService.getGlobalProperty(GP_LOCATION_FILTERING_VALUE, Constants.ANY_VALUE)).thenReturn(
		    "Quelimane%*");
		assertTrue(locationBasedFilterStrategy.isFilterTagValid("Quelimane%24julho"));
		assertFalse(locationBasedFilterStrategy.isFilterTagValid("Namaccura%pebane"));
	}
	
	@Test
	public void createFilterFeedShouldReturnXmlStringWithCorrectValue() throws JAXBException {
		Mockito.when(adminService.getGlobalProperty(GP_LOCATION_FIELDS, DEFAULT_LOCATION_FIELDS)).thenReturn(
		    DEFAULT_LOCATION_FIELDS);
		Location location = new Location();
		final String district = "Quelimane";
		final String name = "24julho";
		String expectedFilterValue = new StringBuilder(district).append(FIELD_DELIMITER).append(name).toString();
		Mockito.when(xmlParseService.createXMLFromFeedFilter(Mockito.any(FeedFilter.class))).thenReturn(
		    mockXmlFilter(expectedFilterValue));
		location.setName(name);
		location.setCountyDistrict(district);
		
		Encounter encounter = new Encounter();
		encounter.setLocation(location);
		
		assertTrue(locationBasedFilterStrategy.createFilterFeed(encounter).contains(expectedFilterValue));
		
		encounter.setLocation(null);
		
		assertNull(locationBasedFilterStrategy.createFilterFeed(encounter));
		
		Obs obs = new Obs();
		obs.setLocation(location);
		assertTrue(locationBasedFilterStrategy.createFilterFeed(obs).contains(expectedFilterValue));
		
		obs.setLocation(null);
		encounter.setLocation(location);
		obs.setEncounter(encounter);
		
		assertTrue(locationBasedFilterStrategy.createFilterFeed(obs).contains(expectedFilterValue));
		
		obs.setEncounter(null);
		
		assertNull(locationBasedFilterStrategy.createFilterFeed(obs));
		
		Visit visit = new Visit();
		visit.setLocation(location);
		assertTrue(locationBasedFilterStrategy.createFilterFeed(visit).contains(expectedFilterValue));
		
		visit.setLocation(null);
		assertNull(locationBasedFilterStrategy.createFilterFeed(visit));
	}
	
	private String mockXmlFilter(final String filterValue) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><something>" + filterValue + "</something>";
	}
}
