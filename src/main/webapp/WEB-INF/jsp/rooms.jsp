<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>Browse Rooms</h2>
    <c:if test="${not empty success}"><p class="success">${success}</p></c:if>
    <c:if test="${not empty error}"><p class="error">${error}</p></c:if>
    <form method="get" class="grid">
        <input name="city" placeholder="City" value="${city}"/>
        <input name="minRent" placeholder="Min Rent" type="number" step="0.01" value="${minRent}"/>
        <input name="maxRent" placeholder="Max Rent" type="number" step="0.01" value="${maxRent}"/>
        <button class="btn" type="submit">Search</button>
    </form>
</section>
<section class="card">
    <ul>
        <c:forEach var="room" items="${rooms}">
            <li>
                <a href="/rooms/${room.id}">${room.title}</a>
                <span>${room.location} - ₹${room.monthlyRent}</span>
            </li>
        </c:forEach>
    </ul>
</section>
<%@ include file="fragments/footer.jspf" %>
