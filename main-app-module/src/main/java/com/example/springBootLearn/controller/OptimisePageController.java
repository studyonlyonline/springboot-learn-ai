// package com.example.springBootLearn.controller;
//
// import com.example.common.service.FirestoreInitializer;
// import com.example.common.util.RequestContextUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.servlet.ModelAndView;
//
// @Controller
// public class OptimisePageController {
//
//     private final FirestoreInitializer firestoreInitializer;
//     private final RequestContextUtil requestContextUtil;
//
//     @Autowired
//     public OptimisePageController(FirestoreInitializer firestoreInitializer, RequestContextUtil requestContextUtil) {
//         this.firestoreInitializer = firestoreInitializer;
//         this.requestContextUtil = requestContextUtil;
//     }
//
//     @GetMapping("/optimise")
//     public ModelAndView getOptimisePage() {
//         ModelAndView modelAndView = new ModelAndView();
//         modelAndView.setViewName("/optimise/listPage");
//
//         // Add request context information to the model
//         modelAndView.addObject("requestId", requestContextUtil.getRequestId());
//         modelAndView.addObject("requestPath", requestContextUtil.getRequestPath());
//
//         return modelAndView;
//     }
// }
