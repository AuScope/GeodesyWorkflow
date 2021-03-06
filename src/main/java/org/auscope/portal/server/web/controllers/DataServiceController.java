package org.auscope.portal.server.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.server.gridjob.GeodesyGridInputFile;
import org.auscope.portal.server.util.PortalPropertyPlaceholderConfigurer;
import org.auscope.portal.server.web.view.JSONModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles all Data Service related requests.
 *
 * @author Abdi Jama
 */
@Controller
public class DataServiceController {
   
   protected final Log logger = LogFactory.getLog(getClass());
   
   @Autowired
   @Qualifier(value = "propertyConfigurer")
   private PortalPropertyPlaceholderConfigurer hostConfigurer;
   

   private List<GeodesyGridInputFile> parseJSONArrayString(String jsonList) {
	   JSONArray items = JSONArray.fromObject(jsonList);
	   
	   List<GeodesyGridInputFile> selection = new ArrayList<GeodesyGridInputFile>();
	   for (Object obj : items) {
		   if (obj instanceof JSONObject) {
			   selection.add(GeodesyGridInputFile.fromJSONObject((JSONObject) obj));
		   }
	   }
	   
	   return selection;
   }
   
   /**
    * 
    * @param request
    * @param response
    * @param selectedList a JSON String representing an array of GeodesyGridInputFile objects
    */
   @RequestMapping("/saveSelection.do")
   public void saveSelection(HttpServletRequest request,
		                     HttpServletResponse response,
		                     @RequestParam("mySelection") String selectedList) {
	   
	   List<GeodesyGridInputFile> selection = parseJSONArrayString(selectedList);
	   
	   request.getSession().setAttribute("selectedGPSfiles", selection);
	   
       logger.debug("Saved user's selected GPS files " + selectedList);
   } 
   
   /**
    * Gets all selected items
    * Returns a JSON Array Response in the following format
    * selections :
    * 		[
    *   		{"stationId": "id", "fileDate" : "1/3/5", "fileUrl" : "http://somewhere", "selected" : true },
    *   		...
    * 		]
    * @param request
    * @param response
    * @throws Exception
    */
   @RequestMapping("/getSelection.do")
   public ModelAndView getSelection(HttpServletRequest request) throws Exception {
	   List<GeodesyGridInputFile> selectedList = (List<GeodesyGridInputFile>) request.getSession().getAttribute("selectedGPSfiles");
	   JSONArray response = new JSONArray();
	   if (selectedList != null) {
		   logger.debug("Return user's selected GPS files "+selectedList);
       
       
		   for (GeodesyGridInputFile ggif : selectedList) {
			   response.add(ggif);
		   }
	   }
       
	   ModelMap map = new ModelMap();
	   map.addAttribute("selections", response);
	   
       return new JSONModelAndView(map);
   } 
   
   /**
    * Sets all selected urls to send to the grid
    * Expects a JSON Array Response in the following format
    * [
    *   {"stationId": "id", "fileDate" : "1/3/5", "fileUrl" : "http://somewhere", "selected" : true },
    *   ...
    * ]
    * @param request
    * @param selectedUrls A JSON String representing an array
    * @throws Exception
    */
   @RequestMapping("/sendToGrid.do")
   public ModelAndView sendToGrid(HttpServletRequest request, String myFiles, ModelMap model)throws Exception {
	   List<GeodesyGridInputFile> selection = parseJSONArrayString(myFiles);
	   
	   logger.debug("selected GPS files for grid job: "+selection);
	   
	   request.getSession().setAttribute("gridInputFiles", selection);

	   model.put("success", true);
	   return new JSONModelAndView(model);
   }
   
   @RequestMapping("/zipDownload.do")
   public ModelAndView zipDownload(HttpServletRequest request,
                           HttpServletResponse response, ModelMap model)throws Exception {
	   String selectedFiles = request.getParameter("myFiles");
	   logger.debug("selected GPS files for zip: "+selectedFiles);
	   //Zip and Download is on hold.
	   /*List<String> urlsList = GeodesyUtil.getSelectedGPSFiles(selectedFiles);

	   if(urlsList == null){
		   logger.error("No files to Zip for download");
		   return;
	   }
	   
       // Create a new directory to zip all files into.
	   //String user = request.getRemoteUser();
	   String user = "user";
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
       String dateFmt = sdf.format(new Date());
       String jobID = user + "-" + dateFmt + File.separator;
       String jobInputDir = jobID;

       boolean success = (new File(jobInputDir)).mkdir();

       if (!success) {
           logger.error("Could not create directory "+jobInputDir);
           jobInputDir = gridAccess.getLocalGridFtpStageInDir();
       }
	   
      
       // These are the files to include in the ZIP file
       String[] filenames =  urlsList.toArray(new String[]{});
       
       // Create a buffer for reading the files
       byte[] buf = new byte[1024];
       ZipOutputStream out = null;
       InputStream in = null;
       try {
           // The ZIP file
           String outFilename = jobInputDir+"outfile.zip";
           out = new ZipOutputStream(new FileOutputStream(outFilename));
           out.setLevel(9);
           // add the files
           for (int i=0; i<filenames.length; i++) {
        	   URL url = new URL(filenames[i]);
        	   logger.debug("Zipping file: "+filenames[i]);
        	   in = url.openStream();
       
               // Add ZIP entry to output stream.
        	   int myIndex = filenames[i].lastIndexOf("/");
        	   String fName = filenames[i].substring(myIndex+1, filenames[i].length());
               out.putNextEntry(new ZipEntry(fName));
               logger.debug("File: "+fName);
       
               // Transfer bytes from the file to the ZIP file
               int len;
               while ((len = in.read(buf)) > 0) {
                   out.write(buf, 0, len);
               }
       
               // Complete the entry
               out.closeEntry();
               in.close();
           }
       
           // Complete the ZIP file
           out.close();
       } catch (IOException e) {
       }finally{
    	   in.close();
    	   out.close();
       }
	   return new ModelAndView("zipDownload");*/
	   model.put("success", true);
	   
	   return new JSONModelAndView(model);
   }
   
}
