<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>Welcome, ${user.name}</h2>
    <p>City: ${user.city}</p>
    <p>Occupation: ${user.occupation}</p>
    <a class="btn" href="/rooms/new">Post a Room</a>
</section>
<section class="card">
    <h3>Your Listings</h3>
    <c:choose>
        <c:when test="${empty myRooms}">
            <p>No room listings yet.</p>
        </c:when>
        <c:otherwise>
            <ul>
                <c:forEach var="room" items="${myRooms}">
                    <li><a href="/rooms/${room.id}">${room.title}</a> - ${room.location}</li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
    <p>
        <c:choose>
            <c:when test="${hasPreference}">Your roommate preference profile is saved.</c:when>
            <c:otherwise><a href="/preferences">Set roommate preferences</a> to improve matches.</c:otherwise>
        </c:choose>
    </p>
</section>
<%@ include file="fragments/footer.jspf" %>
