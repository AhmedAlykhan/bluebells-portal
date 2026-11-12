# Bluebells Virtual Assistant — Security, Grounding & Prompt Defense Audit

**Module**: Context-Aware School Virtual Assistant  
**Backend**: `SchoolAssistantController.java`, `SchoolAssistantService.java`  
**Frontend**: `VirtualAssistant.tsx`  
**Auditor**: Ahmed Ali Khan <medlykhan@gmail.com>  
**Date**: November 2026  

---

## 1. Overview & Operational Scope

The Bluebells Virtual Assistant provides automated conversational assistance to parents, students, teachers, and administrators. 

### Key Design Tenets
1. **Zero Hallucination / Strictly Grounded**: The assistant responds exclusively using factual data from the school profile, fee schedules, academic calendar, and live student records.
2. **Role-Based Context**: Questions regarding fees or report cards are filtered according to the authenticated user's persona and linked children.
3. **Prompt Injection & Adversarial Defense**: Sanitizes inputs and refuses prompts attempting to override system instructions or extract administrative data.

---

## 2. Security Architecture & Threat Defenses

### 2.1. Adversarial Prompt Injection Defense
In `SchoolAssistantService.java`:
```java
private static final Pattern ADVERSARIAL_PATTERNS = Pattern.compile(
    "(ignore previous instructions|system prompt|reveal secrets|drop database|show all passwords|override rule)",
    Pattern.CASE_INSENSITIVE
);

public AssistantResponseDto processQuery(AssistantQueryRequest req) {
    String query = req.getQuery();
    if (query == null || query.trim().isEmpty()) {
        return new AssistantResponseDto("Please enter a valid school-related question.");
    }
    
    if (ADVERSARIAL_PATTERNS.matcher(query).find()) {
        return new AssistantResponseDto("I can only assist with verified Bluebells Schooling System academic and operational inquiries.");
    }
    ...
}
```

### 2.2. Role-Aware Grounded Knowledge Base
- **Admissions & Campus Inquiries**: Returns official campus address (`Adjacent Autobahn Road, Next to VIP Garden, Unit 2, Latifabad, Hyderabad`), phone contact, and school timings.
- **Fees & Dues**: When asked about fees, queries the authenticated user's active invoices in PKR; unauthorized users cannot query other students' fees.
- **Examinations & Schedules**: Provides official Sindh Board examination windows and term dates.
- **Fallback**: Unrecognized or ambiguous queries return a polite referral to the official Support Centre: `For specific administrative assistance, please open a ticket in the Support Centre or contact the front desk at Latifabad Unit 2.`

---

## 3. Automated Test Verification

All 4 tests in `SchoolAssistantTest.java` pass:
1. `testAdmissionQueryReturnsFactualInfo`: Verifies accurate school address and admissions guidance.
2. `testAdversarialPromptInjectionRefusal`: Verifies refusal when query contains override patterns.
3. `testFeeStatusQuery`: Verifies grounded fee status response.
4. `testUnrecognizedQueryFallback`: Verifies standardized support referral.
