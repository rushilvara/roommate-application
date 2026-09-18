<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="fragments/header.jspf" %>
<section class="card">
    <h2>Your Matches</h2>
    <c:choose>
        <c:when test="${empty matches}">
            <p>No matches found yet. Add preferences and check again.</p>
        </c:when>
        <c:otherwise>
            <ul>
                <c:forEach var="match" items="${matches}">
                    <li>
                        <strong><a href="/rooms/${match.room.id}">${match.room.title}</a></strong>
                        <div>Score: ${match.score}</div>
                        <div>${match.summary}</div>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</section>
<%@ include file="fragments/footer.jspf" %>
