package com.github.ansell.restletutils;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class SesameRealmConstants
{
    public static final String OAS_NS = "http://purl.org/oas/";
    
    public static final ValueFactory VF = ValueFactoryImpl.getInstance();
    
    public static final URI OAS_ROLEMAPPEDGROUP = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "roleMappedGroup");
    public static final URI OAS_ROLEMAPPEDUSER = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "roleMappedUser");
    public static final URI OAS_ROLEMAPPEDROLE = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "roleMappedRole");
    public static final URI OAS_GROUP = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS, "Group");
    public static final URI OAS_ROOTGROUP = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS, "RootGroup");
    public static final URI OAS_GROUPNAME = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS, "groupName");
    public static final URI OAS_GROUPDESCRIPTION = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "groupDescription");
    public static final URI OAS_GROUPMEMBERUSER = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "groupMemberUser");
    public static final URI OAS_GROUPMEMBERGROUP = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "groupMemberGroup");
    public static final URI OAS_GROUPINHERITINGROLES = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "groupInheritingRoles");
    public static final URI OAS_USER = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS, "User");
    public static final URI OAS_USERIDENTIFIER = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "userIdentifier");
    public static final URI OAS_USERSECRET = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "userSecret");
    public static final URI OAS_USERFIRSTNAME = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "userFirstName");
    public static final URI OAS_USERLASTNAME = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "userLastName");
    public static final URI OAS_USEREMAIL = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS, "userEmail");
    public static final URI OAS_ROLEMAPPING = SesameRealmConstants.VF.createURI(SesameRealmConstants.OAS_NS,
            "RoleMapping");
    
}
