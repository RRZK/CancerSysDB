<%@ page import="de.cancersysdb.portal.NewsStory" %>
<ckeditor:config var="toolbar_Story">
	[
    { name: 'clipboard',   items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
    { name: 'basicstyles', items : [ 'Bold','Italic','Underline','-','RemoveFormat' ] },
    { name: 'paragraph',   items : [ 'NumberedList','BulletedList' ] },
    { name: 'links',       items : [ 'Link','Unlink' ] },
    '/',
    { name: 'styles',      items : [ 'Format' ] }
    ]
</ckeditor:config>



<div class="fieldcontain ${hasErrors(bean: newsStoryInstance, field: 'headline', 'error')} required">
	<label for="headline">
		<g:message code="newsStory.headline.label" default="Headline" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="headline" required="" value="${newsStoryInstance?.headline}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: newsStoryInstance, field: 'story', 'error')} required">
	<label for="story">
		<g:message code="newsStory.story.label" default="Story" />
		<span class="required-indicator">*</span>
	</label>
%{--
	<g:textField name="story" required="" value="${newsStoryInstance?.story}"/>
--}%

	<ckeditor:editor name="story" toolbar="story" height="400px" width="95%">
		${newsStoryInstance?.story}
	</ckeditor:editor>

</div>
