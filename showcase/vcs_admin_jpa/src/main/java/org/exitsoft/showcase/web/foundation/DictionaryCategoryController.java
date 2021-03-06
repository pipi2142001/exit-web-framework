package org.exitsoft.showcase.web.foundation;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.exitsoft.orm.core.PropertyFilterBuilder;
import org.exitsoft.orm.core.PropertyFilter;
import org.exitsoft.showcase.entity.foundation.DictionaryCategory;
import org.exitsoft.showcase.service.foundation.SystemDictionaryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 字典类别管理Controller
 * 
 * @author vincent
 *
 */
@Controller
@RequestMapping("/foundation/dictionary-category")
public class DictionaryCategoryController {
	
	@Autowired
	private SystemDictionaryManager systemDictionaryManager;
	
	/**
	 * 获取字典类别列表
	 * 
	 * @param pageRequest 分页实体信息
	 * @param request HttpServlet请求
	 * 
	 * @return {@link Page}
	 */
	@RequestMapping("view")
	public Page<DictionaryCategory> view(
			@PageableDefaults(sort = { "id" }, sortDir = Direction.DESC) Pageable pageable,
			HttpServletRequest request) {
		
		List<PropertyFilter> filters = PropertyFilterBuilder.build(request);
		
		request.setAttribute("categoriesList", systemDictionaryManager.getAllDictionaryCategories());
		
		return systemDictionaryManager.searchDictionaryCategoryPage(pageable, filters);
	}
	
	/**
	 * 
	 * 保存或更新字典类别,保存成功后重定向到:foundation/dictionary-category/view
	 * 
	 * @param entity 实体信息
	 * @param parentId 所对应的父类id
	 * @param redirectAttributes spring mvc 重定向属性
	 * 
	 * @return String
	 */
	@RequestMapping("save")
	public String save(@ModelAttribute("entity") DictionaryCategory entity,String parentId,RedirectAttributes redirectAttributes) {
		
		if (StringUtils.isEmpty(parentId)) {
			entity.setParent(null);
		} else {
			entity.setParent(systemDictionaryManager.getDictionaryCategory(parentId));
		}
		
		systemDictionaryManager.saveDictionaryCategory(entity);
		redirectAttributes.addFlashAttribute("message", "保存成功");
		return "redirect:/foundation/dictionary-category/view";
	}
	
	/**
	 * 
	 * 读取字典类别,返回foundation/dictionary-category/read.ftl页面
	 * 
	 * @param model Spring mvc的Model接口，主要是将model的属性返回到页面中
	 * 
	 * @return String
	 */
	@RequestMapping("read")
	public String read(HttpServletRequest request) {
		
		List<PropertyFilter> filters = new ArrayList<PropertyFilter>();
		String id = request.getParameter("id");
		
		if (StringUtils.isNotEmpty(id)) {
			filters.add(PropertyFilterBuilder.build("NES_id", id));
		}
		//展示父类下来框时，不要连自己也在下拉框里
		request.setAttribute("categoriesList", systemDictionaryManager.getAllDictionaryCategories(filters));
		
		return "/foundation/dictionary-category/read";
		
	}
	
	/**
	 * 通过主键id集合删除字典类别,删除成功后重定向到:foundation/dictionary-category/view
	 * 
	 * @param ids 主键id集合
	 * @param redirectAttributes spring mvc 重定向属性
	 * 
	 * @return String
	 */
	@RequestMapping("delete")
	public String delete(@RequestParam("ids")List<String> ids,RedirectAttributes redirectAttributes) {
		systemDictionaryManager.deleteDictionaryCategory(ids);
		redirectAttributes.addFlashAttribute("message", "删除" + ids.size() + "条信息成功");
		return "redirect:/foundation/dictionary-category/view";
	}
	
	/**
	 * 绑定实体数据，如果存在id时获取后从数据库获取记录，进入到相对的C后在将数据库获取的记录填充到相应的参数中
	 * 
	 * @param id 主键ID
	 * 
	 */
	@ModelAttribute("entity")
	public DictionaryCategory bindingModel(@RequestParam(value = "id", required = false)String id) {
		DictionaryCategory dictionaryCategory = new DictionaryCategory();
		
		if (StringUtils.isNotEmpty(id)) {
			dictionaryCategory = systemDictionaryManager.getDictionaryCategory(id);
		}
		
		return dictionaryCategory;
	}
	
}
