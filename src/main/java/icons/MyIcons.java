package icons;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;

public interface MyIcons  {
	
    Icon PortalToolbarIcon = IconLoader.getIcon("/icons/portalToolbar.svg", MyIcons.class);
//    Icon PortalToolbarIcon = IconLoader.getIcon("/icons/portal.svg", MyIcons.class);
    Icon ToggleVisibility = IconLoader.getIcon("/icons/toggleVisibility2.svg", MyIcons.class);

    Icon DomainLarge = IconLoader.getIcon("/icons/domain-large", MyIcons.class);
    Icon EventLarge = IconLoader.getIcon("/icons/event-large.svg", MyIcons.class);
    Icon EventSmall = IconLoader.getIcon("/icons/event-small.svg", MyIcons.class);
    Icon EventSmallPub = IconLoader.getIcon("/icons/event-small-pub.svg", MyIcons.class);
    Icon EventSmallSub = IconLoader.getIcon("/icons/event-small-sub.svg", MyIcons.class);
    Icon EventSmallBoth = IconLoader.getIcon("/icons/event-small-both.svg", MyIcons.class);
    Icon ApiProductLarge = IconLoader.getIcon("/icons/diamond-large.svg", MyIcons.class);
    Icon apiProductSmall = IconLoader.getIcon("/icons/diamond-small.svg", MyIcons.class);
    Icon AppLarge = IconLoader.getIcon("/icons/hex-large.svg", MyIcons.class);
    Icon appSmall = IconLoader.getIcon("/icons/hex-small.svg", MyIcons.class);
    Icon AppSmallPub = IconLoader.getIcon("/icons/hex-small-pub.svg", MyIcons.class);
    Icon AppSmallSub = IconLoader.getIcon("/icons/hex-small-sub.svg", MyIcons.class);
    Icon AppSmallBoth = IconLoader.getIcon("/icons/hex-small-both.svg", MyIcons.class);
    Icon apiLarge = IconLoader.getIcon("/icons/square-large.svg", MyIcons.class);
    Icon apiSmall = IconLoader.getIcon("/icons/square-small.svg", MyIcons.class);
    Icon schemaLarge = IconLoader.getIcon("/icons/triangle-large.svg", MyIcons.class);
    Icon SchemaSmall = IconLoader.getIcon("/icons/triangle-small.svg", MyIcons.class);
    Icon SchemaPrimitive = IconLoader.getIcon("/icons/schema-primitive.svg", MyIcons.class);
    Icon SchemaNone = IconLoader.getIcon("/icons/schema-none.svg", MyIcons.class);
    
    Icon PortalGreen = IconLoader.getIcon("/icons/portal.svg", MyIcons.class);
    Icon AsyncApi2 = IconLoader.getIcon("/icons/asyncapi3.svg", MyIcons.class);

}
