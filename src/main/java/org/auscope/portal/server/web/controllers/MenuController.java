package org.auscope.portal.server.web.controllers;

import java.awt.Menu;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.auscope.portal.server.gridjob.GridAccessController;
import org.auscope.portal.server.util.PortalPropertyPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller that handles all {@link Menu}-related requests,
 *
 * @author Jarek Sanders
 */
@Controller
public class MenuController {
   
   protected final Log logger = LogFactory.getLog(getClass());

   @Autowired
   @Qualifier(value = "propertyConfigurer")
   private PortalPropertyPlaceholderConfigurer hostConfigurer;
  
   @Autowired
   private GridAccessController gridAccess;
   /* Commented out, for the time being we are redirecting Home link to AuScope site
   @RequestMapping("/home.html")
   public ModelAndView menu() {
      logger.info("menu controller started!");
      return new ModelAndView("home");
   }
   */
   
   @RequestMapping("/gmap.html")
   public ModelAndView gmap() {
      String googleKey 
         = hostConfigurer.resolvePlaceholder("HOST.googlemap.key");
      String vocabServiceUrl
         = hostConfigurer.resolvePlaceholder("HOST.vocabService.url");
      String nvclWebService
         = hostConfigurer.resolvePlaceholder("nvcl-web-service.url");
      logger.debug("googleKey: " + googleKey);
      logger.debug("vocabServiceUrl: " + vocabServiceUrl);
      logger.debug("nvclWebServiceUrl: " + nvclWebService);
      
      ModelAndView mav = new ModelAndView("gmap");
      mav.addObject("googleKey", googleKey);
      mav.addObject("vocabServiceUrl", vocabServiceUrl);
      mav.addObject("nvclWebServiceIP", nvclWebService);
      return mav;
   }

   @RequestMapping("/mosaic_image.html")
   public ModelAndView mosaic_image() {
      String googleKey 
         = hostConfigurer.resolvePlaceholder("HOST.googlemap.key");
      logger.debug(googleKey);

      ModelAndView mav = new ModelAndView("mosaic_image");
      mav.addObject("googleKey",googleKey);      
      return mav;
   }
      
   @RequestMapping("/plotted_images.html")
   public ModelAndView plotted_images() {
      String googleKey 
         = hostConfigurer.resolvePlaceholder("HOST.googlemap.key");
      logger.debug(googleKey);

      ModelAndView mav = new ModelAndView("plotted_images");
      mav.addObject("googleKey",googleKey);      
      return mav;
   }
      
   @RequestMapping("/login.html")
   public ModelAndView login(HttpServletRequest request) {
      logger.debug("Shib-Identity-Provider : " + request.getHeader("Shib-Identity-Provider"));

      //return new ModelAndView("login");
      return new ModelAndView("redirect:/gmap.html");
   }
   
   @RequestMapping("/about.html")
   public ModelAndView about(HttpServletRequest request) {

      String appServerHome = request.getSession().getServletContext().getRealPath("/");
      File manifestFile = new File(appServerHome,"META-INF/MANIFEST.MF");
      Manifest mf = new Manifest();
      ModelAndView mav = new ModelAndView("about");
      try {
         mf.read(new FileInputStream(manifestFile));
         Attributes atts = mf.getMainAttributes();
         if (mf != null) {
            mav.addObject("specificationTitle", atts.getValue("Specification-Title"));
            mav.addObject("implementationVersion", atts.getValue("Implementation-Version"));
            mav.addObject("implementationBuild", atts.getValue("Implementation-Build"));
            mav.addObject("buildDate", atts.getValue("buildDate"));
            mav.addObject("buildJdk", atts.getValue("Build-Jdk"));
            mav.addObject("javaVendor", atts.getValue("javaVendor"));
            mav.addObject("builtBy", atts.getValue("Built-By"));
            mav.addObject("osName", atts.getValue("osName"));
            mav.addObject("osVersion", atts.getValue("osVersion"));
                        
            mav.addObject("serverName", request.getServerName());
            mav.addObject("serverInfo", request.getSession().getServletContext().getServerInfo());
            mav.addObject("serverJavaVersion", System.getProperty("java.version"));
            mav.addObject("serverJavaVendor", System.getProperty("java.vendor"));
            mav.addObject("javaHome", System.getProperty("java.home"));
            mav.addObject("serverOsArch", System.getProperty("os.arch"));
            mav.addObject("serverOsName", System.getProperty("os.name"));
            mav.addObject("serverOsVersion", System.getProperty("os.version"));
         } else {
            logger.error("Error reading manifest file.");
         }
      } catch (IOException e) {
         /* ignore, since we'll just leave an empty form */
         e.printStackTrace();
      }
      return mav;
   }
   
   @RequestMapping("/admin.html")
   public ModelAndView admin() {
      return new ModelAndView("admin");
   }
   
   
   @RequestMapping("/access_error.html")
   public ModelAndView access_error() {
      return new ModelAndView("access_error");
   }
   
   @RequestMapping("/data_service_tool.html")
   public ModelAndView data_service_tool() {
      return new ModelAndView("data_service_tool");
   }
   
   @RequestMapping("/gpsview.html")
   public ModelAndView gpsview() {
      return new ModelAndView("gpsview");
   }
   
   /**
    * If the user has valid grid credentials, this function will return a ModelAndView with redirectViewName
    * If the user doesn't have valid credentials they will be redirected to the login page (and afterwards redirected to redirectUrl)
    * 
    * @param request
    * @param redirectViewName
    * @param redirectUrl
    * @return
    */
   private ModelAndView doShibbolethAndSLCSLogin(HttpServletRequest request,String redirectViewName, String redirectUrl) {
       if (gridAccess.isProxyValid(
                   request.getSession().getAttribute("userCred"))) {
           logger.debug("No/invalid action parameter; returning " + redirectViewName + " view.");
           return new ModelAndView(redirectViewName);
       } else {
           request.getSession().setAttribute(
                   "redirectAfterLogin", redirectUrl);
           logger.warn("Proxy not initialized. Redirecting to gridLogin.");
           return new ModelAndView(
                   new RedirectView("/gridLogin.do", true, false, false));
       }
   }
 
   @RequestMapping("/gridsubmit.html")
   public ModelAndView gridsubmit(HttpServletRequest request) {
       // Ensure user has valid grid credentials
	   return doShibbolethAndSLCSLogin(request, "gridsubmit", "/gridsubmit.html");
   }

   @RequestMapping("/joblist.html")
   public ModelAndView joblist(HttpServletRequest request) {
	   return doShibbolethAndSLCSLogin(request, "joblist", "/joblist.html");
   }
   
   @RequestMapping("/dbg/debug.html")
   public ModelAndView debug(HttpServletRequest request) {
	   return new ModelAndView("debug");
   }
   
   @RequestMapping("/dbg/dologin.html")
   public ModelAndView debugLogin(HttpServletRequest request) {
       // Ensure user has valid grid credentials
	   return doShibbolethAndSLCSLogin(request, "debug", "/dbg/debug.html");
   }
}
