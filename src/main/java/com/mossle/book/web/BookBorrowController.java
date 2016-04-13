package com.mossle.book.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mossle.api.user.UserConnector;

import com.mossle.book.persistence.domain.BookBorrow;
import com.mossle.book.persistence.manager.BookBorrowManager;

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
public class BookBorrowController {
    private BookBorrowManager bookBorrowManager;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();
    private UserConnector userConnector;
    private MessageHelper messageHelper;

    @RequestMapping("book-borrow-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bookBorrowManager.pagedQuery(page, propertyFilters);

        model.addAttribute("page", page);

        return "book/book-borrow-list";
    }

    @RequestMapping("book-borrow-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            BookBorrow bookBorrow = bookBorrowManager.get(id);
            model.addAttribute("model", bookBorrow);
        }

        return "book/book-borrow-input";
    }

    @RequestMapping("book-borrow-save")
    public String save(@ModelAttribute BookBorrow bookBorrow,
            @RequestParam Map<String, Object> parameterMap,
            RedirectAttributes redirectAttributes) {
        BookBorrow dest = null;

        Long id = bookBorrow.getId();

        if (id != null) {
            dest = bookBorrowManager.get(id);
            beanMapper.copy(bookBorrow, dest);
        } else {
            dest = bookBorrow;
        }

        bookBorrowManager.save(dest);

        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/book/book-borrow-list.do";
    }

    @RequestMapping("book-borrow-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<BookBorrow> bookBorrows = bookBorrowManager
                .findByIds(selectedItem);

        bookBorrowManager.removeAll(bookBorrows);

        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/book/book-borrow-list.do";
    }

    @RequestMapping("book-borrow-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = bookBorrowManager.pagedQuery(page, propertyFilters);

        List<BookBorrow> bookBorrows = (List<BookBorrow>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("book info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bookBorrows);
        exportor.export(request, response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setBookBorrowManager(BookBorrowManager bookBorrowManager) {
        this.bookBorrowManager = bookBorrowManager;
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
