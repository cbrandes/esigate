package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.RenderingException;

/**
 * TODO Type javadoc
 * 
 * @author satyr
 */
public class CompositeRegion implements IRegion {
    private final List<IRegion> children = new LinkedList<IRegion>();

    /**
     * {@inheritDoc}
     * <p>
     * Processes all children by iteratively invoking
     * {@linkplain IRegion#process(Writer, HttpServletRequest)} on them.
     */
    public void process(Writer out, HttpServletRequest request)
            throws IOException, RenderingException {
        for (IRegion child : children) {
            child.process(out, request);
        }
    }

    public void add(IRegion child) {
        children.add(child);
    }

    public void remove(IRegion child) {
        children.remove(child);
    }

}
