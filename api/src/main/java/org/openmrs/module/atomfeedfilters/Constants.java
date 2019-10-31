package org.openmrs.module.atomfeedfilters;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 10/23/19.
 */
public class Constants {
	
	/**
	 * Global property name for the value that will be used to filter feeds from parent when pulling
	 */
	public static final String GP_LOCATION_FILTERING_VALUE = "atomfeedfilters.location.value";
	
	/**
	 * Global property name for % (per FIELD_DELIMITER) separated location fields that will be used
	 * for filtering.
	 */
	public static final String GP_LOCATION_FIELDS = "atomfeedfilters.location.fields";
	
	/**
	 * The default value for chosen location fields
	 */
	public static final String DEFAULT_LOCATION_FIELDS = "county_district%name";
	
	/**
	 * Character used to indicate any value is acceptable for a particular location field used in
	 * the filter.
	 */
	public static final String ANY_VALUE = "*";
	
	/**
	 * Separates field values, for example county_district%name (means the first value is for
	 * county_district while the second is name)
	 */
	public static final String FIELD_DELIMITER = "%";
}
