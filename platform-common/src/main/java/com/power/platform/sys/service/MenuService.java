package com.power.platform.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.power.platform.common.service.GenericService;
import com.power.platform.sys.entity.Menu;

@Service
@Transactional(readOnly = true)
public interface MenuService extends GenericService<Menu> {

}
