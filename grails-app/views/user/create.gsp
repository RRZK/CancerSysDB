<html>

<head>
    <meta name="layout" content="main"/>
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>
<sec:ifAnyGranted roles="ROLE_ADMIN">

<h3><g:message code="default.create.label" args="[entityName]"/></h3>

<g:form action="save" name='userCreateForm'>

%{--<%
def tabData = []
tabData << [name: 'userinfo', icon: 'icon_user', messageCode: 'spring.security.ui.user.info']
tabData << [name: 'roles',    icon: 'icon_role', messageCode: 'spring.security.ui.user.roles']
%>--}%
<br>
    <div id="tabs" height="375" data="${tabData}">

        <s2ui:tab name='userinfo' height='280'>
            <table>
                <tbody>

                <s2ui:textFieldRow name='userDetail.firstName' labelCode='userDetail.firstName.label' bean="${user}"
                                   labelCodeDefault='First name' value="${user?.details?.firstName}"/>
                <s2ui:textFieldRow name='userDetail.lastName' labelCode='userDetail.lastName.label' bean="${user}"
                                   labelCodeDefault='Last name' value="${user?.details?.lastName}"/>
		
                <s2ui:textFieldRow name='username' labelCode='user.username.label' bean="${user}"
                                   labelCodeDefault='Username' value="${user?.username}"/>

                <s2ui:passwordFieldRow name='password' labelCode='user.password.label' bean="${user}"
                                       labelCodeDefault='Password' value="${user?.password}"/>

                <s2ui:checkboxRow name='enabled' labelCode='user.enabled.label' bean="${user}"
                                  labelCodeDefault='Enabled' value="${user?.enabled}"/>

                <s2ui:checkboxRow name='accountExpired' labelCode='user.accountExpired.label' bean="${user}"
                                  labelCodeDefault='Account expired' value="${user?.accountExpired}"/>

                <s2ui:checkboxRow name='accountLocked' labelCode='user.accountLocked.label' bean="${user}"
                                  labelCodeDefault='Account locked' value="${user?.accountLocked}"/>

                <s2ui:checkboxRow name='passwordExpired' labelCode='user.passwordExpired.label' bean="${user}"
                                  labelCodeDefault='Password expired' value="${user?.passwordExpired}"/>
                </tbody>
            </table>
        </s2ui:tab>

        <s2ui:tab name='roles' height='100'>
            <g:each var="auth" in="${authorityList}">
                <div>
                    <g:checkBox name="${auth.authority}"/>
                    ${auth.authority.encodeAsHTML()}
                </div>
            </g:each>
        </s2ui:tab>

    </div>
    <s2ui:submitButton elementId='create' form='userCreateForm' messageCode='default.button.create.label'/>

</g:form>

<script>
    $(document).ready(function () {
        $('#username').focus();
        <s2ui:initCheckboxes/>
    });
</script>
</sec:ifAnyGranted>
</body>
</html>
