package com.bingchunmoli.api.controller.advice;


/**
 * @author 冰彦糖
 * @version 0.0.1-SNAPSHOT
 * @copyright(c) 2017-2020 冰纯茉莉
 * @description: TODO
 * @data 2020/12/7 18:10
 * @className ControllerAdvice
 * @packageName: com.bingchunmoli.api.controller.advice
 **/
//@Slf4j
//@Aspect
//@Component
public class ControllerAdvice {
//    @Autowired
//    private ITongJiService tongJiService;
//    @Around("execution(public * *..*Controller.*(..))")
//    public Object tongJi(ProceedingJoinPoint proceedingJoinPoint){
//        Object proceed = null;
//        try {
//            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = requestAttributes.getRequest();
//            tongJiService.save(new TongJi(ServletUtil.getClientIP(request, "X-Forwarded-For"), request.getRequestURI(), request.getMethod()));
//            proceed = proceedingJoinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return proceed;
//    }
}