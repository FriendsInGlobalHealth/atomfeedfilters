package org.openmrs.module.atomfeedfilters;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.atomfeed.api.filter.FeedFilterStrategy;
import org.openmrs.module.atomfeed.api.filter.GenericFeedFilterStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        return null;
    }

    @Override
    public boolean isFilterTagValid(String filter) {
        if(filter == null) return false;

        String comparisonValue = adminService.getGlobalProperty(Constants.GP_LOCATION_NAME, ANY_NAME_LOCATION);
        return "".equals(comparisonValue) || ANY_NAME_LOCATION.equals(comparisonValue) || comparisonValue.equalsIgnoreCase(filter);
    }
}
