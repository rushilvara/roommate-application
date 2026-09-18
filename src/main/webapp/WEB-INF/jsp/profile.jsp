<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>Profile</h2>
    <c:if test="${not empty success}"><p class="success">${success}</p></c:if>
    <c:if test="${not empty error}"><p class="error">${error}</p></c:if>
    <form:form method="post" modelAttribute="profileRequest">
        <label>Name</label>
        <form:input path="name"/>
        <form:errors path="name" cssClass="error"/>

        <label>Phone</label>
        <form:input path="phone"/>

        <label>Gender</label>
        <form:input path="gender"/>

        <label>Age</label>
        <form:input path="age" type="number"/>
        <form:errors path="age" cssClass="error"/>

        <label>Occupation</label>
        <form:input path="occupation"/>

        <label>City</label>
        <form:input path="city"/>

        <label>Bio</label>
        <form:textarea path="bio" rows="4"/>

        <button class="btn" type="submit">Save Profile</button>
    </form:form>
</section>
<%@ include file="fragments/footer.jspf" %>
