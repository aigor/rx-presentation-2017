/**
 * Copyright (C) Zoomdata, Inc. 2012-2017. All rights reserved.
 */
package org.aigor.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ThymeleafViews {

   @RequestMapping({"/"})
   public String index() {
      return "visualization";
   }

   @RequestMapping({"/login"})
   public String login() {
      return "login";
   }

   @RequestMapping({"/visualization"})
   public String visualization() {
      return "visualization";
   }

   @RequestMapping({"/app-status"})
   public String appStatus() {
      return "app-status";
   }
}
