/*******************************************************************************
 * Copyright (c) 七月 14 2016 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
 * All rights reserved.
 *
 * Contributors:
 *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation.
 * Auto Generate By foreveross.com Quick Deliver Platform. 
 ******************************************************************************/
package com.foreveross.common.web;

import com.foreveross.common.ConstantBean;
import com.foreveross.common.ResultBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.iff.infra.util.Logger;
import org.iff.infra.util.MapHelper;
import org.iff.infra.util.StreamHelper;
import org.iff.infra.util.StringHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * file upload download.
 *
 * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>
 * @since Aug 9, 2015
 * auto generate by qdp.
 */
@Controller
@RequestMapping("/file")
public class FileController extends BaseController {

    @ResponseBody
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public ResultBean upload(@RequestParam("file") MultipartFile upload, HttpServletRequest request,
                             HttpServletResponse response) {
        String fileName = null;
        if (upload == null) {
            return ResultBean.error().setBody("Unable to upload. File is empty.");
        }
        try {
            String path = ConstantBean.getProperty("file.upload.dir",
                    StringHelper.pathConcat(System.getProperty("java.io.tmpdir"), "/upload/"));
            File parent = new File(path);
            if (!parent.exists()) {
                parent.mkdirs();
            }
            fileName = upload.getOriginalFilename();
            String fileId = StringHelper.uuid();
            {
                int indexOf = StringUtils.lastIndexOf(fileName, '.');
                if (indexOf > -1) {
                    fileName = fileName.substring(0, indexOf) + "-" + fileId + "." + fileName.substring(indexOf + 1);
                }
            }
            byte[] bytes = upload.getBytes();
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            try {
                File file = new File(parent, fileName);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(bytes);
                Logger.debug("file upload: " + file.getAbsolutePath());
                return ResultBean.success().setBody(MapHelper.toMap("id", fileId, "fileName", fileName));
            } finally {
                StreamHelper.closeWithoutError(fos);
                StreamHelper.closeWithoutError(bos);
            }
        } catch (Exception e) {
            return ResultBean.success()
                    .setBody("You failed to upload " + upload.getOriginalFilename() + ": " + e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping(value = "/remove.do", method = RequestMethod.POST)
    public ResultBean remove(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        String fileName = null;
        try {
            //String id = request.getParameter("id");
            fileName = request.getParameter("fileName");
            if (StringUtils.isBlank(fileName)) {
                return ResultBean.error().setBody("Unable to remove upload: fileName is empty.");
            }
            String path = ConstantBean.getProperty("file.upload.dir",
                    StringHelper.pathConcat(System.getProperty("java.io.tmpdir"), "/upload/"));
            File parent = new File(path);
            if (!parent.exists()) {
                parent.mkdirs();
            }
            File file = new File(parent, fileName);
            FileUtils.deleteQuietly(file);
            Logger.debug("upload file removed: " + file.getAbsolutePath());
            return ResultBean.success().setBody("success");
        } catch (Exception e) {
            return ResultBean.success().setBody("You failed to remove upload file " + fileName + ": " + e.getMessage());
        }
    }
}
