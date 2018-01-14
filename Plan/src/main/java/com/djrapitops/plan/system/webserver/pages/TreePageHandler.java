/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plan.system.webserver.pages;

import com.djrapitops.plan.api.exceptions.WebUserAuthException;
import com.djrapitops.plan.system.webserver.Request;
import com.djrapitops.plan.system.webserver.auth.Authentication;
import com.djrapitops.plan.system.webserver.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //TODO Class Javadoc Comment
 *
 * @author Rsl1122
 */
public abstract class TreePageHandler extends PageHandler {

    private Map<String, PageHandler> pages;

    public TreePageHandler() {
        pages = new HashMap<>();
    }

    public void registerPage(String targetPage, PageHandler handler) {
        pages.put(targetPage, handler);
    }

    public void registerPage(String targetPage, Response response, int requiredPerm) {
        pages.put(targetPage, new PageHandler() {
            @Override
            public Response getResponse(Request request, List<String> target) {
                return response;
            }

            @Override
            public boolean isAuthorized(Authentication auth, List<String> target) throws WebUserAuthException {
                return auth.getWebUser().getPermLevel() <= requiredPerm;
            }
        });
    }

    @Override
    public Response getResponse(Request request, List<String> target) {
        PageHandler pageHandler = getPageHandler(target);
        return pageHandler != null
                ? pageHandler.getResponse(request, target)
                : DefaultResponses.NOT_FOUND.get();
    }

    public PageHandler getPageHandler(List<String> target) {
        if (target.isEmpty()) {
            return pages.get("");
        }
        String targetPage = target.get(0);
        target.remove(0);
        return pages.get(targetPage);
    }

    public PageHandler getPageHandler(String targetPage) {
        return pages.get(targetPage);
    }
}