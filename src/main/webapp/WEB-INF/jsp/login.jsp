<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>Login</h2>
    <c:if test="${not empty error}"><p class="error">${error}</p></c:if>
    <form:form method="post" modelAttribute="loginRequest">
        <label>Email</label>
        <form:input path="email" type="email"/>
        <form:errors path="email" cssClass="error"/>

        <label>Password</label>
        <form:password path="password"/>
        <form:errors path="password" cssClass="error"/>

        <button class="btn" type="submit">Login</button>
    </form:form>
</section>
<%@ include file="fragments/footer.jspf" %>
