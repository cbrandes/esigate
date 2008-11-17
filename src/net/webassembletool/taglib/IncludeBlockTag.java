package net.webassembletool.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.webassembletool.DriverFactory;
import net.webassembletool.RenderException;

/**
 * Retrieves an HTML fragment from the provider application and inserts it into
 * the page. Extends AbstractReplaceableTag, so a ReplaceTag can be used inside
 * this tag.
 * 
 * @author Fran�ois-Xavier Bonnet
 * 
 */
public class IncludeBlockTag extends BodyTagSupport implements ReplaceableTag,
	ParametrizableTag, ErrorManageableTag {

    private static final long serialVersionUID = 1L;
    private String name;
    private String page;
    private String provider;
    private Map<Integer, String> errorMap = new HashMap<Integer, String>();
    private Map<String, String> replaceRules = new HashMap<String, String>();
    private Map<String, String> parameters = new HashMap<String, String>();
    private boolean addQuery = false;
    private boolean displayErrorPage = false;
    private boolean parseAbsoluteUrl = true;
    private String defaultErrorMessage;

    public boolean isParseAbsoluteUrl() {
	return parseAbsoluteUrl;
    }

    public void setParseAbsoluteUrl(boolean parseAbsoluteUrl) {
	this.parseAbsoluteUrl = parseAbsoluteUrl;
    }

    public boolean isDisplayErrorPage() {
	return displayErrorPage;
    }

    public void setDisplayErrorPage(boolean displayErrorPage) {
	this.displayErrorPage = displayErrorPage;
    }

    public boolean isAddQuery() {
	return addQuery;
    }

    public void setAddQuery(boolean addQuery) {
	this.addQuery = addQuery;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public int doEndTag() throws JspException {
	if (addQuery) {
	    @SuppressWarnings("unchecked")
	    Map<String, String[]> queryParameters = pageContext.getRequest()
		    .getParameterMap();
	    if ((queryParameters != null) && !queryParameters.isEmpty()) {
		for (Entry<String, String[]> queryParameter : queryParameters
			.entrySet()) {
		    if ((queryParameter.getValue() != null)
			    && (queryParameter.getValue().length > 0)) {
			StringBuilder sb = new StringBuilder();
			for (String value : queryParameter.getValue()) {
			    sb.append(value);
			    sb.append(',');
			}
			if (sb.length() > 0) {
			    parameters.put(queryParameter.getKey(), sb
				    .substring(0, sb.length() - 1));
			} else {
			    parameters.put(queryParameter.getKey(), sb
				    .toString());
			}
		    }
		}
	    }
	}
	if (parseAbsoluteUrl) {
	    if (replaceRules == null) {
		replaceRules = new HashMap<String, String>();
	    }

	    String baseUrl = DriverFactory.getInstance(provider).getBaseURL();
	    int baseUrlEnd = baseUrl.indexOf('/', baseUrl.indexOf("//") + 2);
	    if (baseUrlEnd > 0) {
		baseUrl = baseUrl.substring(0, baseUrlEnd);
	    }
	    replaceRules.put("href=(\"|')/(.*)(\"|')", "href=$1" + baseUrl
		    + "/$2$3");
	}
	try {
	    DriverUtils.renderBlock(provider, page, name, pageContext,
		    replaceRules, parameters);
	} catch (RenderException re) {
	    if (displayErrorPage) {
		try {
		    pageContext.getOut().append(re.getErrorPageContent());
		} catch (IOException e) {
		    throw new JspException(e);
		}
	    } else if (errorMap.containsKey(re.getStatusCode())) {
		try {
		    pageContext.getOut().append(
			    errorMap.get(re.getStatusCode()));
		} catch (IOException e) {
		    throw new JspException(e);
		}
	    } else if (defaultErrorMessage != null
		    && !"".equals(defaultErrorMessage)) {
		try {
		    pageContext.getOut().append(defaultErrorMessage);
		} catch (IOException e) {
		    throw new JspException(e);
		}
	    } else {
		try {
		    pageContext.getOut().write(
			    re.getStatusCode() + " " + re.getStatusMessage());
		} catch (IOException e) {
		    throw new JspException(e);
		}

	    }
	}
	name = null;
	page = null;
	provider = null;
	errorMap = new HashMap<Integer, String>();
	replaceRules = new HashMap<String, String>();
	parameters = new HashMap<String, String>();
	displayErrorPage = false;
	errorMap = new HashMap<Integer, String>();
	defaultErrorMessage = null;
	return EVAL_PAGE;
    }

    public String getPage() {
	return page;
    }

    public void setPage(String page) {
	this.page = page;
    }

    public String getProvider() {
	return provider;
    }

    public void setProvider(String provider) {
	this.provider = provider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.webassembletool.taglib.IReplaceableTag#getReplaceRules()
     */
    public Map<String, String> getReplaceRules() {
	return replaceRules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.webassembletool.taglib.IParameterTag#getParameters()
     */
    public Map<String, String> getParameters() {
	return parameters;
    }

    /**
     * @see net.webassembletool.taglib.ErrorManageableTag#getErrorMap()
     */
    public Map<Integer, String> getErrorMap() {
	return errorMap;
    }

    public String getDefaultMessage() {
	return defaultErrorMessage;
    }

    public void setDefaultMessage(String errorMessage) {
	defaultErrorMessage = errorMessage;

    }

}
