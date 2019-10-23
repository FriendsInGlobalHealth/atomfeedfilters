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

import javax.xml.bind.JAXBException;

import static org.openmrs.module.atomfeedfilters.Constants.ANY_NAME_LOCATION;

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
        if(openmrsObject instanceof Encounter) {
            Encounter encounter = (Encounter) openmrsObject;
            Location location = encounter.getLocation();
            filter = location == null ? null : location.getName();
        } else if (openmrsObject instanceof Visit) {
            Visit visit = (Visit) openmrsObject;
            Location location = visit.getLocation();
            filter = location == null ? null : location.getName();
        } else if (openmrsObject instanceof Obs) {
            Obs obs = (Obs) openmrsObject;
            Location location = obs.getLocation();
            if(location != null) {
                filter =  location.getName();
            }
            if(obs.getEncounter() != null) {
                location = obs.getEncounter().getLocation();
                filter = location == null ? null : location.getName();
            }
        }
        return createFeedFilterXML(filter);
    }

    @Override
    public boolean isFilterTagValid(String filter) {
        if(filter == null) return false;

        String comparisonValue = adminService.getGlobalProperty(Constants.GP_LOCATION_NAME, ANY_NAME_LOCATION);
        return "".equals(comparisonValue) || ANY_NAME_LOCATION.equals(comparisonValue) || comparisonValue.equalsIgnoreCase(filter);
    }

    private String createFeedFilterXML(String filter) {
        if (filter == null) {
            return null;
        }
        FeedFilter feedFilter = new FeedFilter(getBeanName(), filter);

        String xml;
        try {
            xml = getXmlParseService().createXMLFromFeedFilter(feedFilter);
        } catch (JAXBException e) {
            throw new AtomfeedException(e);
        }
        return xml;
    }
}
