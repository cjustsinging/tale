package com.tale.controller.admin;

import com.blade.ioc.annotation.Inject;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.ui.RestResponse;
import com.tale.controller.BaseController;
import com.tale.dto.Types;
import com.tale.exception.TipException;
import com.tale.model.Metas;
import com.tale.service.MetasService;
import com.tale.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by biezhi on 2017/2/21.
 */
@Path("admin/links")
public class LinksController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinksController.class);

    @Inject
    private MetasService metasService;

    @Inject
    private SiteService siteService;

    @GetRoute(values = "")
    public String index(Request request) {
        List<Metas> metass = metasService.getMetas(Types.LINK);
        request.attribute("links", metass);
        return "admin/links";
    }

    @PostRoute(values = "save")
    @JSON
    public RestResponse saveLink(@QueryParam String title, @QueryParam String url,
                                 @QueryParam String logo, @QueryParam Integer mid,
                                 @QueryParam(defaultValue = "0") int sort) {
        try {
            Metas metas = new Metas();
            metas.setName(title);
            metas.setSlug(url);
            metas.setDescription(logo);
            metas.setSort(sort);
            metas.setType(Types.LINK);
            if (null != mid) {
                metas.setMid(mid);
                metasService.update(metas);
            } else {
                metasService.saveMeta(metas);
            }
            siteService.cleanCache(Types.C_STATISTICS);
        } catch (Exception e) {
            String msg = "友链保存失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

    @Route(values = "delete")
    @JSON
    public RestResponse delete(@QueryParam int mid) {
        try {
            metasService.delete(mid);
            siteService.cleanCache(Types.C_STATISTICS);
        } catch (Exception e) {
            String msg = "友链删除失败";
            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

}
