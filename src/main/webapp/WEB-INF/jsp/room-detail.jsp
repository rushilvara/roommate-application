<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <c:if test="${not empty success}"><p class="success">${success}</p></c:if>
    <c:if test="${not empty error}"><p class="error">${error}</p></c:if>
    <h2>${room.title}</h2>
    <p>${room.description}</p>
    <p><strong>Location:</strong> ${room.location}</p>
    <p><strong>Rent:</strong> ₹${room.monthlyRent}</p>
    <p><strong>Available:</strong> ${room.availableFrom}</p>
    <p><strong>Furnished:</strong> ${room.furnished ? 'Yes' : 'No'}</p>

    <c:if test="${isOwner}">
        <a class="btn" href="/rooms/${room.id}/edit">Edit</a>
        <form method="post" action="/rooms/${room.id}/delete" class="inline">
            <button class="btn btn-danger" type="submit">Delete</button>
        </form>
    </c:if>
</section>
<%@ include file="fragments/footer.jspf" %>
