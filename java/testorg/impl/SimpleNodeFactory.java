package testorg.impl;

import testorg.*;
import testorg.factory.*;

public class SimpleNodeFactory implements MkNodes {
    private static final SimpleNodeFactory shared = new SimpleNodeFactory();

    protected SimpleNodeFactory() {
    }

    public static SimpleNodeFactory getInstance() {
        return shared;
    }


    public Uri mkUri(String u) {
        return new Uri() {
            public String value() {
                return u;
            }
        };
    };

}
