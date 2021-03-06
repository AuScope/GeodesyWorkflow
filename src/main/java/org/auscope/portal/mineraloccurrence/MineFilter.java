package org.auscope.portal.mineraloccurrence;

/**
 * User: Mathew Wyatt
 * Date: 23/03/2009
 * Time: 1:59:02 PM
 */
public class MineFilter implements IFilter {
    private String mineName;

    /**
     * Given a mine name, this object will build a filter to do a PropertyIsLike serach for mine names
     *
     * @param mineName
     */
    public MineFilter(String mineName) {
        this.mineName = mineName;
        //this.kvps = "service=WFS&version=1.1.0&request=GetFeature&typeName=mo:Mine&namespace=xmlns(mo=urn:cgi:xmlns:GGIC:MineralOccurrence:1.0)";
    }


    public String getFilterString() {
        return  "        <ogc:Filter " +
                "                   xmlns:er=\"urn:cgi:xmlns:GGIC:EarthResource:1.1\" " +
                "                   xmlns:ogc=\"http://www.opengis.net/ogc\" " +
                "                   xmlns:gml=\"http://www.opengis.net/gml\" " +
                "                   xmlns:xlink=\"http://www.w3.org/1999/xlink\" " +
                "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "                <ogc:PropertyIsLike wildCard=\"*\" singleChar=\"#\" escapeChar=\"!\">\n" +
                "                    <ogc:PropertyName>er:mineName/er:MineName/er:mineName</ogc:PropertyName>\n" +
                "                    <ogc:Literal>" + mineName + "</ogc:Literal>\n" +
                "                </ogc:PropertyIsLike>\n" +
                "        </ogc:Filter>";

    }
    
}
