package group1.intern.util.util;

import group1.intern.util.constant.CommonConstant;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebUtils {
    /*
     * Http Session
     */
    public static class Sessions {
        private static HttpSession getSession() {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        }

        public static void setCurrentGetUrl(String url) {
            var session = getSession();
            session.setAttribute(CommonConstant.PREVIOUS_GET_URL, session.getAttribute(CommonConstant.CURRENT_GET_URL));
            session.setAttribute(CommonConstant.CURRENT_GET_URL, url);
        }

        public static void setAttribute(String name, Object value) {
            var session = getSession();
            session.setAttribute(name, value);
        }

        @SuppressWarnings("unchecked")
        public static <T> T getAttribute(String name, Class<T> clazz) {
            try {
                var session = getSession();
                return (T) session.getAttribute(name);
            } catch (Exception e) {
                return null;
            }
        }

        public static void removeAttribute(String name) {
            var session = getSession();
            session.removeAttribute(name);
        }

        public static void removeAllData() {
            var session = getSession();
            session.getAttributeNames().asIterator().forEachRemaining(session::removeAttribute);
        }

    }


    /*
     * Cookie
     */
    public static class Cookies {
        private static HttpServletResponse getResponse() {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        }

        public static Cookie[] getCookies() {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getCookies();
        }

        public static Cookie getCookie(String name) {
            Cookie[] cookies = getCookies();
            if (CommonUtils.isNotEmptyOrNullList(cookies)) {
                for (Cookie cookie : cookies)
                    if (cookie.getName().equals(name)) return cookie;
            }
            return null;
        }

        public static void setCookie(String name, String value) {
            var resp = getResponse();
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
            resp.addCookie(cookie);
        }

        public static void removeCookie(String name) {
            var resp = getResponse();
            Cookie cookie = new Cookie(name, "");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
        }
    }


    private WebUtils() {
    }
}
