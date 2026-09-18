<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>${roomId == null ? 'Post Room' : 'Edit Room'}</h2>
    <form:form method="post" modelAttribute="roomRequest" action="${roomId == null ? '/rooms' : '/rooms/'.concat(roomId)}">
        <label>Title</label>
        <form:input path="title"/>
        <form:errors path="title" cssClass="error"/>

        <label>Description</label>
        <form:textarea path="description" rows="4"/>
        <form:errors path="description" cssClass="error"/>

        <label>Location</label>
        <form:input path="location"/>
        <form:errors path="location" cssClass="error"/>

        <label>Monthly Rent</label>
        <form:input path="monthlyRent" type="number" step="0.01"/>
        <form:errors path="monthlyRent" cssClass="error"/>

        <label>Available From</label>
        <form:input path="availableFrom" type="date"/>
        <form:errors path="availableFrom" cssClass="error"/>

        <label class="checkbox">
            <form:checkbox path="furnished"/> Furnished
        </label>

        <button class="btn" type="submit">Save</button>
    </form:form>
</section>
<%@ include file="fragments/footer.jspf" %>
