<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>Roommate Preferences</h2>
    <c:if test="${not empty success}"><p class="success">${success}</p></c:if>

    <form:form method="post" modelAttribute="preferenceRequest">
        <label>Preferred City</label>
        <form:input path="preferredCity"/>

        <label>Minimum Budget</label>
        <form:input path="minBudget" type="number" step="0.01"/>

        <label>Maximum Budget</label>
        <form:input path="maxBudget" type="number" step="0.01"/>

        <label>Preferred Gender</label>
        <form:input path="preferredGender"/>

        <label>Smoking Allowed</label>
        <form:select path="smokingAllowed">
            <form:option value="" label="No Preference"/>
            <form:option value="true" label="Yes"/>
            <form:option value="false" label="No"/>
        </form:select>

        <label>Pets Allowed</label>
        <form:select path="petsAllowed">
            <form:option value="" label="No Preference"/>
            <form:option value="true" label="Yes"/>
            <form:option value="false" label="No"/>
        </form:select>

        <label>Cleanliness Preference</label>
        <form:input path="cleanlinessPreference" placeholder="Neat / Moderate / Flexible"/>

        <label>Sleep Schedule</label>
        <form:input path="sleepSchedule" placeholder="Early / Late / Flexible"/>

        <button class="btn" type="submit">Save Preferences</button>
    </form:form>
</section>
<%@ include file="fragments/footer.jspf" %>
