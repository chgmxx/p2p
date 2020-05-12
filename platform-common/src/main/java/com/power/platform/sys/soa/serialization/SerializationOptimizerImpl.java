package com.power.platform.sys.soa.serialization;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.alibaba.dubbo.common.serialize.support.SerializationOptimizer;
import com.power.platform.sys.entity.Area;
import com.power.platform.sys.entity.Dict;
import com.power.platform.sys.entity.Log;
import com.power.platform.sys.entity.Menu;
import com.power.platform.sys.entity.Office;
import com.power.platform.sys.entity.Role;
import com.power.platform.sys.entity.User;

public class SerializationOptimizerImpl implements SerializationOptimizer {

    @SuppressWarnings("rawtypes")
	public Collection<Class> getSerializableClasses() {
        List<Class> classes = new LinkedList<Class>();
        classes.add(User.class);
        classes.add(Role.class);
        classes.add(Area.class);
        classes.add(Office.class);
        classes.add(Menu.class);
        classes.add(Dict.class);
        classes.add(Log.class);
        classes.add(Log.class);
//        classes.add(OrgDomainRelatioin.class);
//        classes.add(UserPosiRelation.class);
//        classes.add(UserRoleRelation.class);
//        classes.add(OrgRoleRelation.class);
//        classes.add(RoleResRelation.class);
        return classes;
    }
}
