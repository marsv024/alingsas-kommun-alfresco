package se.alingsas.alfresco.share.evaluator.documentlibrary.action;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

public class IsPublicSite extends BaseEvaluator {
  @Override
  public boolean evaluate(JSONObject jsonObject) {
    String siteId = getSiteId(jsonObject);

    RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();

    String userId = requestContext.getUserId();

    try {
      Connector connector = requestContext.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());

      Response response = connector.call("/api/sites/" + siteId);

      int code = response.getStatus().getCode();

      if (code == Status.STATUS_OK) {
        JSONObject json = (JSONObject) JSONValue.parseWithException(response.getResponse());

        String visibility = (String) json.get("visibility");
        return visibility.equals("PUBLIC");
      }
    } catch (Exception e) {
      e.getMessage();
    }

    return false;
  }


}
