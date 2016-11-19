package com.mossle.book.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.book.persistence.domain.BookInfo;
import com.mossle.book.persistence.manager.BookInfoManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.query.PropertyFilter;
import com.mossle.core.spring.MessageHelper;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("book")
public class BookInfoController {
    private BookInfoManager bookInfoManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("book-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bookInfoManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "book/book-info-list";
    }

    @RequestMapping("book-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BookInfo bookInfo = bookInfoManager.get(id);
            model.addAttribute("model", bookInfo);
        }

        return "book/book-info-input";
    }

    @RequestMapping("book-info-save")
    public String save(@ModelAttribute BookInfo bookInfo,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        BookInfo dest = null;

        Long id = bookInfo.getId();

        if (id != null) {
            dest = bookInfoManager.get(id);
            beanMapper.copy(bookInfo, dest);
        } else {
            dest = bookInfo;
        }

        bookInfoManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/book/book-info-list.do";
    }

    @RequestMapping("book-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<BookInfo> bookInfos = bookInfoManager.findByIds(selectedItem);

        bookInfoManager.removeAll(bookInfos);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/book/book-info-list.do";
    }

    @RequestMapping("book-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bookInfoManager.pagedQuery(page, propertyFilters);

        List<BookInfo> bookInfos = (List<BookInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("book info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bookInfos);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setBookInfoManager(BookInfoManager bookInfoManager) {
        this.bookInfoManager = bookInfoManager;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }

    @Resource
    public void setUserConnector(UserConnector userConnector) {
        this.userConnector = userConnector;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }
}
