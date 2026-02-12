package pl.orderops.orderops.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class TemplateRenderer {

  public String render(String template, String payload) {

    DocumentContext ctx = JsonPath.parse(payload);

    Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
    Matcher matcher = pattern.matcher(template);

    StringBuffer result = new StringBuffer();

    while (matcher.find()) {
      String path = matcher.group(1).trim().replace("payload.", "$.");
      Object value = ctx.read(path);
      matcher.appendReplacement(result, value == null ? "" : value.toString());
    }

    matcher.appendTail(result);
    return result.toString();
  }
}
