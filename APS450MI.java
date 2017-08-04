/*
***************************************************************
*                                                             *
*                           NOTICE                            *
*                                                             *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
*   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
*   ALL OTHER RIGHTS RESERVED.                                *
*                                                             *
*   (c) COPYRIGHT 2013 INFOR.  ALL RIGHTS RESERVED.           *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
*                                                             *
***************************************************************
*/
package mvx.app.pgm;

import mvx.app.common.*;
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.util.*;

/*
*Modification area - M3
*Nbr            Date   User id     Description*     JT-554162 140226 EONG        Due date not transferred with APS450MI
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>Api: Supplier invoice batch</FONT></B><BR><BR>
*
* This class provides an API to supplier invoice batch. <BR><BR>
*
*/
public class APS450MI extends MIBatch
{
   public void movexMain() {
      INIT();
      //   Accept conversation
      MICommon.initiate();
      MICommon.accept();
      while (MICommon.read()) {
         //   Execute command
         if (MICommon.isTransaction(GET_USER_INFO)) {
            MICommon.setTransaction(retrieveUserInfo.getMessage());
         } else if (MICommon.isTransaction("AddHead")) {
            addHead();
         } else if (MICommon.isTransaction("AddLine")) {
            addLine();
         } else if (MICommon.isTransaction("AddAddInfo")) {
            addAddInfo();
         } else if (MICommon.isTransaction("LstInvBatchNo")) {
            lstInvBatchNo();
         } else if (MICommon.isTransaction("LstInvBySupInv")) {
            lstInvBySupInv();
         } else if (MICommon.isTransaction("PrintInvoice")) {
            printInvoice();
         } else if (MICommon.isTransaction("GetHead")) {
            getHead();
         } else if (MICommon.isTransaction("LstLines")) {
            lstLines();
         } else if (MICommon.isTransaction("Acknowledge")) {
            acknowledge();
         } else if (MICommon.isTransaction("ApproveInvoice")) {
            approveInvoice();
         } else if (MICommon.isTransaction("RejectInvoice")) {
            rejectInvoice();
         } else if (MICommon.isTransaction("AdjustLine")) {
            adjustLine();
         } else if (MICommon.isTransaction("LstClaimDetails")) {
            lstClaimDetails();
         } else {
            MICommon.setTransactionError();
         }
         MICommon.write();
      }
      //   Deallocate
      MICommon.close();
      SETLR();
      return;
   }

   /**
   * Execute command AddHead.
   */
   public void addHead() {
      sAPS450MIRAddHead inAddHead = (sAPS450MIRAddHead)MICommon.getInDS(sAPS450MIRAddHead.class);
      sAPS450MISAddHead outAddHead = (sAPS450MISAddHead)MICommon.getOutDS(sAPS450MISAddHead.class);
      boolean newEntryContext = false;
      // Call APS450Fnc, maintain - mode ADD, step INITIATE
      // =========================================
      pAPS450Fnc_maintain = get_pAPS450Fnc_maintain();
      pAPS450Fnc_maintain.messages.forgetNotifications();
      pAPS450Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.INITIATE);
      pAPS450Fnc_maintain.indicateAutomated();
      // - Partial validation - PVLD
      if (!MICommon.set(pAPS450Fnc_maintain.partialVld, inAddHead.getQ8PVLD(), "PVLD")) {
         return;
      }
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS450Fnc_maintain.DIVI, inAddHead.getQ8DIVI())) {
         return;
      }
      // Set other parameters
      // - Payee
      if (!MICommon.set(pAPS450Fnc_maintain.SPYN, inAddHead.getQ8SPYN())) {
         return;
      }
      // - Supplier
      if (!MICommon.set(pAPS450Fnc_maintain.SUNO, inAddHead.getQ8SUNO())) {
         return;
      }
      // - Invoice date
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.IVDT, inAddHead.getQ8IVDT(), "IVDT")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.IVDT, inAddHead.getQ8IVDT(), "IVDT")) {
            return;
         }
      }
      // - Invoice batch type
      if (!MICommon.set(pAPS450Fnc_maintain.IBTP, inAddHead.getQ8IBTP())) {
         return;
      }
      // - Purchase order number
      if (!MICommon.set(pAPS450Fnc_maintain.PUNO, inAddHead.getQ8PUNO())) {
         return;
      }
      // Invoice matching
      if (!MICommon.set(pAPS450Fnc_maintain.IMCD, inAddHead.getQ8IMCD())) {
         return;
      }
      // - Get payee defaults
      if (!MICommon.set(pAPS450Fnc_maintain.GPDF, inAddHead.getQ8GPDF(), "GPDF")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      addHead_handleMessages(pAPS450Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, maintain - mode ADD, step VALIDATE
      // =========================================
      do {
         pAPS450Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.VALIDATE);
         // Move fields to function parameters
         addHead_getInData(inAddHead);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         addHead_handleMessages(pAPS450Fnc_maintain.messages);
         newEntryContext = pAPS450Fnc_maintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS450Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      addHead_checkIfInvalidFieldsSet(inAddHead);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, maintain - mode ADD, step UPDATE
      // =========================================
      pAPS450Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS450Fnc_maintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS450Fnc_maintain.messages, transStatus);
      // =========================================
      // Handle messages
      addHead_handleMessages(pAPS450Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Return data
      // =========================================
      addHead_setOutData(outAddHead);
      MICommon.setData(outAddHead.get());
   }

   /**
   * Get in data for command AddHead.
   * @param inAddHead
   *    DS for in data.
   */
   public void addHead_getInData(sAPS450MIRAddHead inAddHead) {
      // - Supplier invoice number.
      if (!MICommon.set(pAPS450Fnc_maintain.SINO, inAddHead.getQ8SINO())) {
         return;
      }
      // Currency
      if (!MICommon.set(pAPS450Fnc_maintain.CUCD, inAddHead.getQ8CUCD())) {
         return;
      }
      // Exchange rate
      if (!MICommon.set(pAPS450Fnc_maintain.ARAT, inAddHead.getQ8ARAT(), "ARAT")) {
         return;
      }
      // Payment terms
      if (!MICommon.set(pAPS450Fnc_maintain.TEPY, inAddHead.getQ8TEPY())) {
         return;
      }
      // Payment mtd AP
      if (!MICommon.set(pAPS450Fnc_maintain.PYME, inAddHead.getQ8PYME())) {
         return;
      }
      // Trade code
      if (!MICommon.set(pAPS450Fnc_maintain.TDCD, inAddHead.getQ8TDCD())) {
         return;
      }
      // Foreign currency amount
      if (!MICommon.set(pAPS450Fnc_maintain.CUAM, inAddHead.getQ8CUAM(), "CUAM")) {
         return;
      }
      // VAT amount
      if (!MICommon.set(pAPS450Fnc_maintain.VTAM, inAddHead.getQ8VTAM(), "VTAM")) {
         return;
      }
      // Accounting date
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.ACDT, inAddHead.getQ8ACDT(), "ACDT")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.ACDT, inAddHead.getQ8ACDT(), "ACDT")) {
            return;
         }
      }
      // Authorizer
      if (!MICommon.set(pAPS450Fnc_maintain.APCD, inAddHead.getQ8APCD())) {
         return;
      }
      // Service code
      if (!MICommon.set(pAPS450Fnc_maintain.SERS, inAddHead.getQ8SERS(), "SERS")) {
         return;
      }
      // Due date
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.DUDT, inAddHead.getQ8DUDT(), "DUDT")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.DUDT, inAddHead.getQ8DUDT(), "DUDT")) {
            return;
         }
      }
      // Future exchange contract
      if (!MICommon.set(pAPS450Fnc_maintain.FECN, inAddHead.getQ8FECN())) {
         return;
      }
      // Exchange rate type
      if (!MICommon.set(pAPS450Fnc_maintain.CRTP, inAddHead.getQ8CRTP(), "CRTP")) {
         return;
      }
      // From/To country
      if (!MICommon.set(pAPS450Fnc_maintain.FTCO, inAddHead.getQ8FTCO())) {
         return;
      }
      // Base country
      if (!MICommon.set(pAPS450Fnc_maintain.BSCD, inAddHead.getQ8BSCD())) {
         return;
      }
      // Order date
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.PUDT, inAddHead.getQ8PUDT(), "PUDT")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.PUDT, inAddHead.getQ8PUDT(), "PUDT")) {
            return;
         }
      }
      // Cash discount terms
      if (!MICommon.set(pAPS450Fnc_maintain.TECD, inAddHead.getQ8TECD())) {
         return;
      }
      // Cash discount date 1
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.CDT1, inAddHead.getQ8CDT1(), "CDT1")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.CDT1, inAddHead.getQ8CDT1(), "CDT1")) {
            return;
         }
      }
      // Cash discount percentage 1
      if (!MICommon.set(pAPS450Fnc_maintain.CDP1, inAddHead.getQ8CDP1(), "CDP1")) {
         return;
      }
      // Cash discount amount 1
      if (!MICommon.set(pAPS450Fnc_maintain.CDC1, inAddHead.getQ8CDC1(), "CDC1")) {
         return;
      }
      // Cash discount date 2
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.CDT2, inAddHead.getQ8CDT2(), "CDT2")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.CDT2, inAddHead.getQ8CDT2(), "CDT2")) {
            return;
         }
      }
      // Cash discount percentage 2
      if (!MICommon.set(pAPS450Fnc_maintain.CDP2, inAddHead.getQ8CDP2(), "CDP2")) {
         return;
      }
      // Cash discount amount 2
      if (!MICommon.set(pAPS450Fnc_maintain.CDC2, inAddHead.getQ8CDC2(), "CDC2")) {
         return;
      }
      // Cash discount date 3
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.CDT3, inAddHead.getQ8CDT3(), "CDT3")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.CDT3, inAddHead.getQ8CDT3(), "CDT3")) {
            return;
         }
      }
      // Cash discount percentage 3
      if (!MICommon.set(pAPS450Fnc_maintain.CDP3, inAddHead.getQ8CDP3(), "CDP3")) {
         return;
      }
      // Cash discount amount 3
      if (!MICommon.set(pAPS450Fnc_maintain.CDC3, inAddHead.getQ8CDC3(), "CDC3")) {
         return;
      }
      // Total taxable amount
      if (!MICommon.set(pAPS450Fnc_maintain.TTXA, inAddHead.getQ8TTXA(), "TTXA")) {
         return;
      }
      // Cash discount base
      if (!MICommon.set(pAPS450Fnc_maintain.TASD, inAddHead.getQ8TASD(), "TASD")) {
         return;
      }
      // Pre-paid amount
      if (!MICommon.set(pAPS450Fnc_maintain.PRPA, inAddHead.getQ8PRPA(), "PRPA")) {
         return;
      }
      // VAT registration number
      if (!MICommon.set(pAPS450Fnc_maintain.VRNO, inAddHead.getQ8VRNO())) {
         return;
      }
      // Tax applicable
      if (!MICommon.set(pAPS450Fnc_maintain.TXAP, inAddHead.getQ8TXAP(), "TXAP")) {
         return;
      }
      // Document code
      if (!MICommon.set(pAPS450Fnc_maintain.DNCO, inAddHead.getQ8DNCO())) {
         return;
      }
      // AP Standard document
      if (!MICommon.set(pAPS450Fnc_maintain.SDAP, inAddHead.getQ8SDAP())) {
         return;
      }
      // Debit note reason
      if (!MICommon.set(pAPS450Fnc_maintain.DNRE, inAddHead.getQ8DNRE())) {
         return;
      }
      // Our invoicing address
      if (!MICommon.set(pAPS450Fnc_maintain.PYAD, inAddHead.getQ8PYAD())) {
         return;
      }
      // Text line 1
      if (!MICommon.set(pAPS450Fnc_maintain.SDA1, inAddHead.getQ8SDA1())) {
         return;
      }
      // Text line 2
      if (!MICommon.set(pAPS450Fnc_maintain.SDA2, inAddHead.getQ8SDA2())) {
         return;
      }
      // Text line 3
      if (!MICommon.set(pAPS450Fnc_maintain.SDA3, inAddHead.getQ8SDA3())) {
         return;
      }
      // EAN location code payee
      if (!MICommon.set(pAPS450Fnc_maintain.EALP, inAddHead.getQ8EALP())) {
         return;
      }
      // EAN location code consignee
      if (!MICommon.set(pAPS450Fnc_maintain.EALR, inAddHead.getQ8EALR())) {
         return;
      }
      // EAN location code supplier
      if (!MICommon.set(pAPS450Fnc_maintain.EALS, inAddHead.getQ8EALS())) {
         return;
      }
      // Delivery date
      if (pAPS450Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS450Fnc_maintain.DEDA, inAddHead.getQ8DEDA(), "DEDA")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS450Fnc_maintain.DEDA, inAddHead.getQ8DEDA(), "DEDA")) {
            return;
         }
      }
      // Bank account ID
      if (!MICommon.set(pAPS450Fnc_maintain.BKID, inAddHead.getQ8BKID())) {
         return;
      }
      // Geographical code
      if (!MICommon.set(pAPS450Fnc_maintain.GEOC, inAddHead.getQ8GEOC(), "GEOC")) {
         return;
      }
      // Tax included
      if (!MICommon.set(pAPS450Fnc_maintain.TXIN, inAddHead.getQ8TXIN(), "TXIN")) {
         return;
      }
      // Original invoice number
      if (!MICommon.set(pAPS450Fnc_maintain.DNOI, inAddHead.getQ8DNOI())) {
         return;
      }
      // Original year
      if (!MICommon.set(pAPS450Fnc_maintain.OYEA, inAddHead.getQ8OYEA(), "OYEA")) {
         return;
      }
      // Reference number
      if (!MICommon.set(pAPS450Fnc_maintain.PPYR, inAddHead.getQ8PPYR())) {
         return;
      }
      // Payment request number
      if (!MICommon.set(pAPS450Fnc_maintain.PPYN, inAddHead.getQ8PPYN())) {
         return;
      }
      // Year
      if (!MICommon.set(pAPS450Fnc_maintain.YEA4, inAddHead.getQ8YEA4(), "YEA4")) {
         return;
      }
      // Voucher number
      if (!MICommon.set(pAPS450Fnc_maintain.VONO, inAddHead.getQ8VONO(), "VONO")) {
         return;
      }
      // - CorrelationID
      if (!MICommon.set(pAPS450Fnc_maintain.CORI, inAddHead.getQ8CORI())) {
         return;
      }
      
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command AddHead.
   * @param inAddHead
   *    DS for in data.
   */
   public void addHead_checkIfInvalidFieldsSet(sAPS450MIRAddHead inAddHead) {
      // Currency.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CUCD, inAddHead.getQ8CUCD(), "CUCD")) {
         return;
      }
      // Exchange rate.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.ARAT, inAddHead.getQ8ARAT(), "ARAT")) {
         return;
      }
      // Payment terms.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TEPY, inAddHead.getQ8TEPY(), "TEPY")) {
         return;
      }
      // Payment mtd AP.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.PYME, inAddHead.getQ8PYME(), "PYME")) {
         return;
      }
      // Trade code.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TDCD, inAddHead.getQ8TDCD(), "TDCD")) {
         return;
      }
      // Foreign currency amount.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CUAM, inAddHead.getQ8CUAM(), "CUAM")) {
         return;
      }
      // VAT amount.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.VTAM, inAddHead.getQ8VTAM(), "VTAM")) {
         return;
      }
      // Accounting date.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.ACDT, inAddHead.getQ8ACDT(), "ACDT")) {
         return;
      }
      // Authorizer.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.APCD, inAddHead.getQ8APCD(), "APCD")) {
         return;
      }
      // Invoice matching.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.FTCO, inAddHead.getQ8FTCO(), "FTCO")) {
         return;
      }
      // Service code.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.SERS, inAddHead.getQ8SERS(), "SERS")) {
         return;
      }
      // Due date.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.DUDT, inAddHead.getQ8DUDT(), "DUDT")) {
         return;
      }
      // Future exchange contract.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.FECN, inAddHead.getQ8FECN(), "FECN")) {
         return;
      }
      // Exchange rate type.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CRTP, inAddHead.getQ8CRTP(), "CRTP")) {
         return;
      }
      // From/To country.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.FTCO, inAddHead.getQ8FTCO(), "FTCO")) {
         return;
      }
      // Base country.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.BSCD, inAddHead.getQ8BSCD(), "BSCD")) {
         return;
      }
      // Order date.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.PUDT, inAddHead.getQ8PUDT(), "PUDT")) {
         return;
      }
      // Cash discount terms.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TECD, inAddHead.getQ8TECD(), "TECD")) {
         return;
      }
      // Cash discount date 1.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.CDT1, inAddHead.getQ8CDT1(), "CDT1")) {
         return;
      }
      // Cash discount percentage 1.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CDP1, inAddHead.getQ8CDP1(), "CDP1")) {
         return;
      }
      // Cash discount amount 1.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CDC1, inAddHead.getQ8CDC1(), "CDC1")) {
         return;
      }
      // Cash discount date 2.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.CDT2, inAddHead.getQ8CDT2(), "CDT2")) {
         return;
      }
      // Cash discount percentage 2.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CDP2, inAddHead.getQ8CDP2(), "CDP2")) {
         return;
      }
      // Cash discount amount 2.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CDC2, inAddHead.getQ8CDC2(), "CDC2")) {
         return;
      }
      // Cash discount date 3.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.CDT3, inAddHead.getQ8CDT3(), "CDT3")) {
         return;
      }
      // Cash discount percentage 3.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CDP3, inAddHead.getQ8CDP3(), "CDP3")) {
         return;
      }
      // Cash discount amount 3.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CDC3, inAddHead.getQ8CDC3(), "CDC3")) {
         return;
      }
      // Total taxable amount.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TTXA, inAddHead.getQ8TTXA(), "TTXA")) {
         return;
      }
      // Cash discount base.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TASD, inAddHead.getQ8TASD(), "TASD")) {
         return;
      }
      // Pre-paid amount.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.PRPA, inAddHead.getQ8PRPA(), "PRPA")) {
         return;
      }
      // VAT registration number
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.VRNO, inAddHead.getQ8VRNO(), "VRNO")) {
         return;
      }
      // Tax applicable.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TXAP, inAddHead.getQ8TXAP(), "TXAP")) {
         return;
      }
      // Document code.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.DNCO, inAddHead.getQ8DNCO(), "DNCO")) {
         return;
      }
      // AP Standard document.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.SDAP, inAddHead.getQ8SDAP(), "SDAP")) {
         return;
      }
      // Debit note reason.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.DNRE, inAddHead.getQ8DNRE(), "DNRE")) {
         return;
      }
      // Our invoicing address.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.PYAD, inAddHead.getQ8PYAD(), "PYAD")) {
         return;
      }
      // Text line 1.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.SDA1, inAddHead.getQ8SDA1(), "SDA1")) {
         return;
      }
      // Text line 2.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.SDA2, inAddHead.getQ8SDA2(), "SDA2")) {
         return;
      }
      // Text line 3.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.SDA3, inAddHead.getQ8SDA3(), "SDA3")) {
         return;
      }
      // EAN location code payee.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.EALP, inAddHead.getQ8EALP(), "EALP")) {
         return;
      }
      // EAN location code consignee.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.EALR, inAddHead.getQ8EALR(), "EALR")) {
         return;
      }
      // EAN location code supplier.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.EALS, inAddHead.getQ8EALS(), "EALS")) {
         return;
      }
      // Delivery date.
      if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS450Fnc_maintain.DEDA, inAddHead.getQ8DEDA(), "DEDA")) {
         return;
      }
      // Bank account ID
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.BKID, inAddHead.getQ8BKID(), "BKID")) {
         return;
      }
      // Geographical code.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.GEOC, inAddHead.getQ8GEOC(), "GEOC")) {
         return;
      }
      // Tax included.
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.TXIN, inAddHead.getQ8TXIN(), "TXIN")) {
         return;
      }
      // CorrelationID
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_maintain.CORI, inAddHead.getQ8CORI(), "CORI")) {
         return;
      }
   }

   /**
   * Set out data for command AddHead.
   * @param outAddHead
   *    DS for out data.
   */
   public void addHead_setOutData(sAPS450MISAddHead outAddHead) {
      outAddHead.clear();
      // Invoice batch number
      if (!pAPS450Fnc_maintain.INBN.isAccessDISABLED()) {
         outAddHead.setY8INBN().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.INBN.get()));
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void addHead_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, addHead_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String addHead_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command AddLine.
   */
   public void addLine() {
      sAPS450MIRAddLine inAddLine = (sAPS450MIRAddLine)MICommon.getInDS(sAPS450MIRAddLine.class);
      sAPS450MISAddLine outAddLine = (sAPS450MISAddLine)MICommon.getOutDS(sAPS450MISAddLine.class);
      boolean newEntryContext = false;
      // Call APS451Fnc, maintain - mode ADD, step INITIATE
      // =========================================
      pAPS451Fnc_maintain = get_pAPS451Fnc_maintain();
      pAPS451Fnc_maintain.messages.forgetNotifications();
      pAPS451Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.INITIATE);
      pAPS451Fnc_maintain.indicateAutomated();
      // - Partial validation - PVLD
      if (!MICommon.set(pAPS451Fnc_maintain.partialVld, inAddLine.getQ9PVLD(), "PVLD")) {
         return;
      }
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS451Fnc_maintain.DIVI, inAddLine.getQ9DIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS451Fnc_maintain.INBN, inAddLine.getQ9INBN(), "INBN")) {
         return;
      }
      // Set other parameters
      // Line type
      if (!MICommon.set(pAPS451Fnc_maintain.RDTP, inAddLine.getQ9RDTP(), "RDTP")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAPS451Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      addLine_handleMessages(pAPS451Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS451Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS451Fnc, maintain - mode ADD, step VALIDATE
      // =========================================
      do {
         pAPS451Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.VALIDATE);
         // Move fields to function parameters
         addLine_getInData(inAddLine);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS451Fnc", pAPS451Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         addLine_handleMessages(pAPS451Fnc_maintain.messages);
         newEntryContext = pAPS451Fnc_maintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS451Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      addLine_checkIfInvalidFieldsSet(inAddLine);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS451Fnc, maintain - mode ADD, step UPDATE
      // =========================================
      pAPS451Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS451Fnc_maintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS451Fnc_maintain.messages, transStatus);
      // =========================================
      // Handle messages
      addLine_handleMessages(pAPS451Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS451Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Return data
      // =========================================
      addLine_setOutData(outAddLine);
      MICommon.setData(outAddLine.get());
   }

   /**
   * Get in data for command AddLine.
   * @param inAddLine
   *    DS for in data.
   */
   public void addLine_getInData(sAPS450MIRAddLine inAddLine) {
      // Service code
      if (!MICommon.set(pAPS451Fnc_maintain.SERS, inAddLine.getQ9SERS(), "SERS")) {
         return;
      }
      // Net amount
      if (!MICommon.set(pAPS451Fnc_maintain.NLAM, inAddLine.getQ9NLAM(), "NLAM")) {
         return;
      }
      // VAT amount 1
      if (!MICommon.set(pAPS451Fnc_maintain.VTA1, inAddLine.getQ9VTA1(), "VTA1")) {
         return;
      }
      // VAT amount 2
      if (!MICommon.set(pAPS451Fnc_maintain.VTA2, inAddLine.getQ9VTA2(), "VTA2")) {
         return;
      }
      // VAT rate 1
      if (!MICommon.set(pAPS451Fnc_maintain.VTP1, inAddLine.getQ9VTP1(), "VTP1")) {
         return;
      }
      // VAT rate 2
      if (!MICommon.set(pAPS451Fnc_maintain.VTP2, inAddLine.getQ9VTP2(), "VTP2")) {
         return;
      }
      // VAT code
      if (!MICommon.set(pAPS451Fnc_maintain.VTCD, inAddLine.getQ9VTCD(), "VTCD")) {
         return;
      }
      // Purchase order number
      if (!MICommon.set(pAPS451Fnc_maintain.PUNO, inAddLine.getQ9PUNO())) {
         return;
      }
      // PO line
      if (!MICommon.set(pAPS451Fnc_maintain.PNLI, inAddLine.getQ9PNLI(), "PNLI")) {
         return;
      }
      // PO line sub number
      if (!MICommon.set(pAPS451Fnc_maintain.PNLS, inAddLine.getQ9PNLS(), "PNLS")) {
         return;
      }
      // Invoiced qty
      if (!MICommon.set(pAPS451Fnc_maintain.IVQA, inAddLine.getQ9IVQA(), "IVQA")) {
         return;
      }
      // U/M (Invoiced qty)
      if (!MICommon.set(pAPS451Fnc_maintain.PUUN, inAddLine.getQ9PUUN())) {
         return;
      }
      // Gross price
      if (!MICommon.set(pAPS451Fnc_maintain.GRPR, inAddLine.getQ9GRPR(), "GRPR")) {
         return;
      }
      // U/M (Gross price)
      if (!MICommon.set(pAPS451Fnc_maintain.PPUN, inAddLine.getQ9PPUN())) {
         return;
      }
      // Net price
      if (!MICommon.set(pAPS451Fnc_maintain.NEPR, inAddLine.getQ9NEPR(), "NEPR")) {
         return;
      }
      // Purchase price qty
      if (!MICommon.set(pAPS451Fnc_maintain.PUCD, inAddLine.getQ9PUCD(), "PUCD")) {
         return;
      }
      // Gross amount
      if (!MICommon.set(pAPS451Fnc_maintain.GLAM, inAddLine.getQ9GLAM(), "GLAM")) {
         return;
      }
      // Discount
      if (!MICommon.set(pAPS451Fnc_maintain.DIPC, inAddLine.getQ9DIPC(), "DIPC")) {
         return;
      }
      // Discount amount
      if (!MICommon.set(pAPS451Fnc_maintain.DIAM, inAddLine.getQ9DIAM(), "DIAM")) {
         return;
      }
      // Invoiced catch weight
      if (!MICommon.set(pAPS451Fnc_maintain.IVCW, inAddLine.getQ9IVCW(), "IVCW")) {
         return;
      }
      // Item number
      if (!MICommon.set(pAPS451Fnc_maintain.ITNO, inAddLine.getQ9ITNO())) {
         return;
      }
      // Alias number
      if (!MICommon.set(pAPS451Fnc_maintain.POPN, inAddLine.getQ9POPN())) {
         return;
      }
      // Self billing agreement number
      if (!MICommon.set(pAPS451Fnc_maintain.SBAN, inAddLine.getQ9SBAN())) {
         return;
      }
      // Sequence no
      if (!MICommon.set(pAPS451Fnc_maintain.CDSE, inAddLine.getQ9CDSE(), "CDSE")) {
         return;
      }
      // Costing element
      if (!MICommon.set(pAPS451Fnc_maintain.CEID, inAddLine.getQ9CEID())) {
         return;
      }
      // Receiving number
      if (!MICommon.set(pAPS451Fnc_maintain.REPN, inAddLine.getQ9REPN(), "REPN")) {
         return;
      }
      // Receipt type
      if (!MICommon.set(pAPS451Fnc_maintain.RELP, inAddLine.getQ9RELP(), "RELP")) {
         return;
      }
      // Deliver note number
      if (!MICommon.set(pAPS451Fnc_maintain.SUDO, inAddLine.getQ9SUDO())) {
         return;
      }
      // Delivery note date
      if (pAPS451Fnc_maintain.partialVld.get()) {
         if (!MICommon.setDate_allowIncorrect(pAPS451Fnc_maintain.DNDT, inAddLine.getQ9DNDT(), "DNDT")) {
            return;
         }
      } else {
         if (!MICommon.setDate(pAPS451Fnc_maintain.DNDT, inAddLine.getQ9DNDT(), "DNDT")) {
            return;
         }
      }
      // Claim number
      if (!MICommon.set(pAPS451Fnc_maintain.CLAN, inAddLine.getQ9CLAN())) {
         return;
      }
      // Claim line
      if (!MICommon.set(pAPS451Fnc_maintain.CLLN, inAddLine.getQ9CLLN(), "CLLN")) {
         return;
      }
      // Transaction number
      if (!MICommon.set(pAPS451Fnc_maintain.TRNO, inAddLine.getQ9TRNO(), "TRNO")) {
         return;
      }
      // Charge text
      if (!MICommon.set(pAPS451Fnc_maintain.CHGT, inAddLine.getQ9CHGT())) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command AddLine.
   * @param inAddLine
   *    DS for in data.
   */
   public void addLine_checkIfInvalidFieldsSet(sAPS450MIRAddLine inAddLine) {
      // Service code
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.SERS, inAddLine.getQ9SERS(), "SERS")) {
         return;
      }
      // Net amount
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.NLAM, inAddLine.getQ9NLAM(), "NLAM")) {
         return;
      }
      // VAT amount 1
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.VTA1, inAddLine.getQ9VTA1(), "VTA1")) {
         return;
      }
      // VAT amount 2
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.VTA2, inAddLine.getQ9VTA2(), "VTA2")) {
         return;
      }
      // VAT rate 1
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.VTP1, inAddLine.getQ9VTP1(), "VTP1")) {
         return;
      }
      // VAT rate 2
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.VTP2, inAddLine.getQ9VTP2(), "VTP2")) {
         return;
      }
      // VAT code
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.VTCD, inAddLine.getQ9VTCD(), "VTCD")) {
         return;
      }
      // Purchase order number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.PUNO, inAddLine.getQ9PUNO(), "PUNO")) {
         return;
      }
      // PO line
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.PNLI, inAddLine.getQ9PNLI(), "PNLI")) {
         return;
      }
      // PO line sub number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.PNLS, inAddLine.getQ9PNLS(), "PNLS")) {
         return;
      }
      // Invoiced qty
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.IVQA, inAddLine.getQ9IVQA(), "IVQA")) {
         return;
      }
      // U/M (Invoiced qty)
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.PUUN, inAddLine.getQ9PUUN(), "PUUN")) {
         return;
      }
      // Gross price
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.GRPR, inAddLine.getQ9GRPR(), "GRPR")) {
         return;
      }
      // U/M (Gross price)
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.PPUN, inAddLine.getQ9PPUN(), "PPUN")) {
         return;
      }
      // Net price
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.NEPR, inAddLine.getQ9NEPR(), "NEPR")) {
         return;
      }
      // Purchase price qty
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.PUCD, inAddLine.getQ9PUCD(), "PUCD")) {
         return;
      }
      // Gross amount
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.GLAM, inAddLine.getQ9GLAM(), "GLAM")) {
         return;
      }
      // Discount
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.DIPC, inAddLine.getQ9DIPC(), "DIPC")) {
         return;
      }
      // Discount amount
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.DIAM, inAddLine.getQ9DIAM(), "DIAM")) {
         return;
      }
      // Invoiced catch weight
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.IVCW, inAddLine.getQ9IVCW(), "IVCW")) {
         return;
      }
      // Item number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.ITNO, inAddLine.getQ9ITNO(), "ITNO")) {
         return;
      }
      // Alias number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.POPN, inAddLine.getQ9POPN(), "POPN")) {
         return;
      }
      // Self billing agreement number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.SBAN, inAddLine.getQ9SBAN(), "SBAN")) {
         return;
      }
      // Sequence no
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.CDSE, inAddLine.getQ9CDSE(), "CDSE")) {
         return;
      }
      // Costing element
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.CEID, inAddLine.getQ9CEID(), "CEID")) {
         return;
      }
      // Receiving number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.REPN, inAddLine.getQ9REPN(), "REPN")) {
         return;
      }
      // Receipt type
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.RELP, inAddLine.getQ9RELP(), "RELP")) {
         return;
      }
      // Deliver note number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.SUDO, inAddLine.getQ9SUDO(), "SUDO")) {
         return;
      }
      // Delivery note date
       if (MICommon.checkIfDateNotAllowed_allowIncorrect(pAPS451Fnc_maintain.DNDT, inAddLine.getQ9DNDT(), "DNDT")) {
         return;
      }
      // Claim number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.CLAN, inAddLine.getQ9CLAN(), "CLAN")) {
         return;
      }
      // Claim line
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.CLLN, inAddLine.getQ9CLLN(), "CLLN")) {
         return;
      }
      // Transaction number
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.TRNO, inAddLine.getQ9TRNO(), "TRNO")) {
         return;
      }
      // Charge text
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_maintain.CHGT, inAddLine.getQ9CHGT(), "CHGT")) {
         return;
      }
   }

   /**
   * Set out data for command AddLine.
   * @param outAddLine
   *    DS for out data.
   */
   public void addLine_setOutData(sAPS450MISAddLine outAddLine) {
      outAddLine.clear();
      // Transaction number
      if (!pAPS451Fnc_maintain.TRNO.isAccessDISABLED()) {
         outAddLine.setY9TRNO().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.TRNO.get()));
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void addLine_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, addLine_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String addLine_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command AddAddInfo.
   */
   public void addAddInfo() {
      sAPS450MIRAddAddInfo inAddAddInfo = (sAPS450MIRAddAddInfo)MICommon.getInDS(sAPS450MIRAddAddInfo.class);
      boolean newEntryContext = false;
      // Call APS453Fnc, maintain - mode ADD, step INITIATE
      // =========================================
      pAPS453Fnc_maintain = get_pAPS453Fnc_maintain();
      pAPS453Fnc_maintain.messages.forgetNotifications();
      pAPS453Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.INITIATE);
      pAPS453Fnc_maintain.indicateAutomated();
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS453Fnc_maintain.DIVI, inAddAddInfo.getQBDIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS453Fnc_maintain.INBN, inAddAddInfo.getQBINBN(), "INBN")) {
         return;
      }
      // - AP Extra info number
      if (!MICommon.set(pAPS453Fnc_maintain.PEXN, inAddAddInfo.getQBPEXN(), "PEXN")) {
         return;
      }
      // - AP extra information
      if (!MICommon.set(pAPS453Fnc_maintain.PEXI, inAddAddInfo.getQBPEXI())) {
         return;
      }
      // - AP Extra info sequence number
      if (!MICommon.set(pAPS453Fnc_maintain.PEXS, inAddAddInfo.getQBPEXS(), "PEXS")) {
         return;
      }
      // Set other parameters
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS453Fnc", pAPS453Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      addAddInfo_handleMessages(pAPS453Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS453Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS453Fnc, maintain - mode ADD, step VALIDATE
      // =========================================
      do {
         pAPS453Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.VALIDATE);
         // Move fields to function parameters
         addAddInfo_getInData(inAddAddInfo);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS453Fnc", pAPS453Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         addAddInfo_handleMessages(pAPS453Fnc_maintain.messages);
         newEntryContext = pAPS453Fnc_maintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS453Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      addAddInfo_checkIfInvalidFieldsSet(inAddAddInfo);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS453Fnc, maintain - mode ADD, step UPDATE
      // =========================================
      pAPS453Fnc_maintain.prepare(cEnumMode.ADD, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS453Fnc_maintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS453Fnc_maintain.messages, transStatus);
      // =========================================
      // Handle messages
      addAddInfo_handleMessages(pAPS453Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS453Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Return data
      // =========================================
   }

   /**
   * Get in data for command AddAddInfo.
   * @param inAddAddInfo
   *    DS for in data.
   */
   public void addAddInfo_getInData(sAPS450MIRAddAddInfo inAddInfo) {
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command AddLine.
   * @param inAddLine
   *    DS for in data.
   */
   public void addAddInfo_checkIfInvalidFieldsSet(sAPS450MIRAddAddInfo inAddAddInfo) {
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void addAddInfo_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, addAddInfo_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String addAddInfo_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command LstInvBatchNo
   */
   public void lstInvBatchNo() {
      sAPS450MIRLstInvBatchNo inLstInvBatchNo = (sAPS450MIRLstInvBatchNo)MICommon.getInDS(sAPS450MIRLstInvBatchNo.class);
      sAPS450MISLstInvBatchNo outLstInvBatchNo = (sAPS450MISLstInvBatchNo)MICommon.getOutDS(sAPS450MISLstInvBatchNo.class);
      // Check fields
      // - Company
      APIBH.setCONO(LDAZD.CONO);
      // - Division
      APIBH.setDIVI().moveLeftPad(inLstInvBatchNo.getQ0DIVI());
      // - Payee
      if (inLstInvBatchNo.getQ0SPYN().isBlank()) {
         // MSGID=WPY1402 Payee must be entered
         MICommon.setError("SPYN", "WPY1402");
         return;
      }
      APIBH.setSPYN().moveLeftPad(inLstInvBatchNo.getQ0SPYN());
      // - Supplier
      APIBH.setSUNO().moveLeftPad(inLstInvBatchNo.getQ0SUNO());
      // - Invoice batch type
      APIBH.setIBTP().moveLeftPad(inLstInvBatchNo.getQ0IBTP());
      // - Invoice status
      int SUPA = 0;
      if (!inLstInvBatchNo.getQ0SUPA().isBlank()) {
         if (MICommon.toNumeric(inLstInvBatchNo.getQ0SUPA())) {
            SUPA = MICommon.getInt();
         } else {
            // Numeric error
            MICommon.setError("SUPA");
            return;
         }
      }
      APIBH.setSUPA(SUPA);
      // - Invoice batch no
      long INBN = 0L;
      if (!inLstInvBatchNo.getQ0INBN().isBlank()) {
         if (MICommon.toNumeric(inLstInvBatchNo.getQ0INBN())) {
            INBN = MICommon.getLong();
         } else {
            // Numeric error
            MICommon.setError("INBN");
            return;
         }
      }
      APIBH.setINBN(INBN);
      // List Invoices (for a specific payee - SPYN)
      int noOfInv = 0;
      APIBH.SETLL("30", APIBH.getKey("30"));
      while (noOfInv < MICommon.getMaxRecords() &&
             APIBH.READE("30", APIBH.getKey("30", 3)))
      {
         noOfInv++;
         // Call APS450Fnc, maintain - mode RETRIEVE, step INITIATE
         // =========================================
         pAPS450Fnc_maintain = get_pAPS450Fnc_maintain();
         pAPS450Fnc_maintain.messages.forgetNotifications();
         pAPS450Fnc_maintain.prepare(cEnumMode.RETRIEVE, cEnumStep.INITIATE);
         // Pass a reference to the record.
         pAPS450Fnc_maintain.APIBH = APIBH;
         pAPS450Fnc_maintain.passFAPIBH = true;
         passExtensionTable(); // exit point for modification
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         lstInvBatchNo_handleMessages(pAPS450Fnc_maintain.messages);
         // Release resources allocated by the parameter list.
         pAPS450Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // Return data
         // =========================================
         lstInvBatchNo_setOutData(outLstInvBatchNo);
         MICommon.setData(outLstInvBatchNo.get());
         MICommon.reply();
      }
      // Empty reply buffer
      MICommon.clearBuffer();
   }

   /**
   * Set out data for command LstInvBatchNo.
   * @param outLstInvBatchNo
   *    DS for out data.
   */
   public void lstInvBatchNo_setOutData(sAPS450MISLstInvBatchNo outLstInvBatchNo) {
      outLstInvBatchNo.clear();
      // Invoice batch number
      if (!pAPS450Fnc_maintain.INBN.isAccessDISABLED()) {
         outLstInvBatchNo.setY0INBN().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.INBN.get()));
      }
      // Supplier
      if (!pAPS450Fnc_maintain.SUNO.isAccessDISABLED()) {
         outLstInvBatchNo.setY0SUNO().moveLeftPad(pAPS450Fnc_maintain.SUNO.get());
      }
      // Supplier invoice number
      if (!pAPS450Fnc_maintain.SINO.isAccessDISABLED()) {
         outLstInvBatchNo.setY0SINO().moveLeftPad(pAPS450Fnc_maintain.SINO.get());
      }
      // Invoice batch type
      if (!pAPS450Fnc_maintain.IBTP.isAccessDISABLED()) {
         outLstInvBatchNo.setY0IBTP().moveLeftPad(pAPS450Fnc_maintain.IBTP.get());
      }
      // Invoice status
      if (!pAPS450Fnc_maintain.SUPA.isAccessDISABLED()) {
         outLstInvBatchNo.setY0SUPA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.SUPA.get()));
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void lstInvBatchNo_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, lstInvBatchNo_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String lstInvBatchNo_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command LstInvBySupInv
   */
   public void lstInvBySupInv() {
      sAPS450MIRLstInvBySupInv inLstInvBySupInv = (sAPS450MIRLstInvBySupInv)MICommon.getInDS(sAPS450MIRLstInvBySupInv.class);
      sAPS450MISLstInvBySupInv outLstInvBySupInv = (sAPS450MISLstInvBySupInv)MICommon.getOutDS(sAPS450MISLstInvBySupInv.class);
      // Check fields
      // - Company
      APIBH.setCONO(LDAZD.CONO);
      // - Division
      APIBH.setDIVI().moveLeftPad(inLstInvBySupInv.getQ1DIVI());
      // - Payee
      if (inLstInvBySupInv.getQ1SPYN().isBlank()) {
         // MSGID=WPY1402 Payee must be entered
         MICommon.setError("SPYN", "WPY1402");
         return;
      }
      APIBH.setSPYN().moveLeftPad(inLstInvBySupInv.getQ1SPYN());
      // - Supplier
      if (inLstInvBySupInv.getQ1SUNO().isBlank()) {
         // MSGID=WSU0102 Supplier must be entered
         MICommon.setError("SUNO", "WSU0102");
         return;
      }
      APIBH.setSUNO().moveLeftPad(inLstInvBySupInv.getQ1SUNO());
      // - Supplier invoice number
      if (inLstInvBySupInv.getQ1SINO().isBlank()) {
         // MSGID=WSI1702 Supplier invoice number must be entered
         MICommon.setError("SINO", "WSI1702");
         return;
      }
      APIBH.setSINO().moveLeftPad(inLstInvBySupInv.getQ1SINO());
      // List Invoices (for a specific supplier invoice number - SINO)
      int noOfInv = 0;
      APIBH.SETLL("40", APIBH.getKey("40", 5));
      while (noOfInv < MICommon.getMaxRecords() &&
             APIBH.READE("40", APIBH.getKey("40", 5)))
      {
         noOfInv++;
         // Call APS450Fnc, maintain - mode RETRIEVE, step INITIATE
         // =========================================
         pAPS450Fnc_maintain = get_pAPS450Fnc_maintain();
         pAPS450Fnc_maintain.messages.forgetNotifications();
         pAPS450Fnc_maintain.prepare(cEnumMode.RETRIEVE, cEnumStep.INITIATE);
         // Pass a reference to the record.
         pAPS450Fnc_maintain.APIBH = APIBH;
         pAPS450Fnc_maintain.passFAPIBH = true;
         passExtensionTable(); // exit point for modification
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         lstInvBySupInv_handleMessages(pAPS450Fnc_maintain.messages);
         // Release resources allocated by the parameter list.
         pAPS450Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // Return data
         // =========================================
         lstInvBySupInv_setOutData(outLstInvBySupInv);
         MICommon.setData(outLstInvBySupInv.get());
         MICommon.reply();
      }
      // Empty reply buffer
      MICommon.clearBuffer();
   }

   /**
   * Set out data for command LstInvBySupInv.
   * @param outLstInvBySupInv
   *    DS for out data.
   */
   public void lstInvBySupInv_setOutData(sAPS450MISLstInvBySupInv outLstInvBySupInv) {
      outLstInvBySupInv.clear();
      // Invoice batch number
      if (!pAPS450Fnc_maintain.INBN.isAccessDISABLED()) {
         outLstInvBySupInv.setY1INBN().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.INBN.get()));
      }
      // Invoice batch type
      if (!pAPS450Fnc_maintain.IBTP.isAccessDISABLED()) {
         outLstInvBySupInv.setY1IBTP().moveLeftPad(pAPS450Fnc_maintain.IBTP.get());
      }
      // Invoice status
      if (!pAPS450Fnc_maintain.SUPA.isAccessDISABLED()) {
         outLstInvBySupInv.setY1SUPA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.SUPA.get()));
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void lstInvBySupInv_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, lstInvBySupInv_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String lstInvBySupInv_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command PrintInvoice.
   */
   public void printInvoice() {
      sAPS450MIRPrintInvoice inPrintInvoice = (sAPS450MIRPrintInvoice)MICommon.getInDS(sAPS450MIRPrintInvoice.class);
      boolean newEntryContext = false;
      // Call APS455Fnc, maintain - mode CHANGE, step INITIATE
      // =========================================
      pAPS455Fnc_maintain = get_pAPS455Fnc_maintain();
      pAPS455Fnc_maintain.messages.forgetNotifications();
      pAPS455Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pAPS455Fnc_maintain.indicateAutomated();
      // Don't update selection record
      pAPS455Fnc_maintain.noUpdate.set(true);
      // Set key parameters
      // - Responsible
      pAPS455Fnc_maintain.RESP.set().moveLeftPad(this.DSUSS);
      // - Report Version
      pAPS455Fnc_maintain.LIVR.set().moveLeftPad(this.DSUSS);
      // Set selection parameters
      // - Division
      if (!MICommon.set(pAPS455Fnc_maintain.selectDIVI, inPrintInvoice.getQ2DIVI())) {
         return;
      }
      // - Invoice batch number
      if (inPrintInvoice.getQ2INBN().isBlank()) {
         // MSGID=WINBN02 Invoice batch number must be entered
         MICommon.setError("INBN", "WINBN02");
         return;
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromINBN, inPrintInvoice.getQ2INBN(), "INBN")) {
         return;
      }
      // - Invoice batch operation
      pAPS455Fnc_maintain.IBOP.set(cRefIBOPext.PRINT());
      // - Report layout
      if (inPrintInvoice.getQ2LITP().isBlank()) {
         // MSGID=WLI0502 Report layout must be entered
         MICommon.setError("LITP", "WLI0502");
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pAPS455Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      printInvoice_handleMessages(pAPS455Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS455Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS455Fnc, maintain - mode CHANGE, step VALIDATE
      // =========================================
      do {
         pAPS455Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.VALIDATE);
         // Move fields to function parameters
         printInvoice_getInData(inPrintInvoice);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS455Fnc", pAPS455Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         printInvoice_handleMessages(pAPS455Fnc_maintain.messages);
         newEntryContext = pAPS455Fnc_maintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS455Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      printInvoice_checkIfInvalidFieldsSet(inPrintInvoice);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS455Fnc, maintain - mode CHANGE, step UPDATE
      // =========================================
      pAPS455Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS455Fnc_maintain.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS455Fnc_maintain.messages, transStatus);
      // =========================================
      // Handle messages
      printInvoice_handleMessages(pAPS455Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS455Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
   }

   /**
   * Get in data for command PrintInvoice.
   * @param inPrintInvoice
   *    DS for in data.
   */
   public void printInvoice_getInData(sAPS450MIRPrintInvoice inPrintInvoice) {
      // Report layout
      if (!MICommon.set(pAPS455Fnc_maintain.LITP, inPrintInvoice.getQ2LITP(), "LITP")) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command PrintInvoice.
   * @param inPrintInvoice
   *    DS for in data.
   */
   public void printInvoice_checkIfInvalidFieldsSet(sAPS450MIRPrintInvoice inPrintInvoice) {
      // Report layout
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.LITP, inPrintInvoice.getQ2LITP(), "LITP")) {
         return;
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void printInvoice_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, printInvoice_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String printInvoice_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command GetHead.
   */
   public void getHead() {
      sAPS450MIRGetHead inGetHead = (sAPS450MIRGetHead)MICommon.getInDS(sAPS450MIRGetHead.class);
      sAPS450MISGetHead outGetHead = (sAPS450MISGetHead)MICommon.getOutDS(sAPS450MISGetHead.class);
      // Call APS450Fnc, maintain - mode RETRIEVE, step INITIATE
      // =========================================
      pAPS450Fnc_maintain = get_pAPS450Fnc_maintain();
      pAPS450Fnc_maintain.messages.forgetNotifications();
      pAPS450Fnc_maintain.prepare(cEnumMode.RETRIEVE, cEnumStep.INITIATE);
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS450Fnc_maintain.DIVI, inGetHead.getQ3DIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS450Fnc_maintain.INBN, inGetHead.getQ3INBN(), "INBN")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      getHead_handleMessages(pAPS450Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Return data
      // =========================================
      getHead_setOutData(outGetHead);
      MICommon.setData(outGetHead.get());
   }

   /**
   * Set out data for command GetHead.
   * @param outGetHead
   *    DS for out data.
   */
   public void getHead_setOutData(sAPS450MISGetHead outGetHead) {
      outGetHead.clear();
      // Invoice batch type
      if (!pAPS450Fnc_maintain.IBTP.isAccessDISABLED()) {
         outGetHead.setY3IBTP().moveLeftPad(pAPS450Fnc_maintain.IBTP.get());
      }
      // Invoice status
      if (!pAPS450Fnc_maintain.SUPA.isAccessDISABLED()) {
         outGetHead.setY3SUPA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.SUPA.get()));
      }
      // Invoice batch status
      if (!pAPS450Fnc_maintain.BIST.isAccessDISABLED()) {
         outGetHead.setY3BIST().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.BIST.get()));
      }
      // Invoice batch head errors
      if (!pAPS450Fnc_maintain.IBHE.isAccessDISABLED()) {
         outGetHead.setY3IBHE().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.IBHE.get()));
      }
      // Invoice batch line errors
      if (!pAPS450Fnc_maintain.IBLE.isAccessDISABLED()) {
         outGetHead.setY3IBLE().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.IBLE.get()));
      }
      // Supplier invoice number
      if (!pAPS450Fnc_maintain.SINO.isAccessDISABLED()) {
         outGetHead.setY3SINO().moveLeftPad(pAPS450Fnc_maintain.SINO.get());
      }
      // Payee
      if (!pAPS450Fnc_maintain.SPYN.isAccessDISABLED()) {
         outGetHead.setY3SPYN().moveLeftPad(pAPS450Fnc_maintain.SPYN.get());
      }
      // Supplier
      if (!pAPS450Fnc_maintain.SUNO.isAccessDISABLED()) {
         outGetHead.setY3SUNO().moveLeftPad(pAPS450Fnc_maintain.SUNO.get());
      }
      // Invoice date
      if (!pAPS450Fnc_maintain.IVDT.isAccessDISABLED()) {
         outGetHead.setY3IVDT().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.IVDT.get()));
      }
      // Currency
      if (!pAPS450Fnc_maintain.CUCD.isAccessDISABLED()) {
         outGetHead.setY3CUCD().moveLeftPad(pAPS450Fnc_maintain.CUCD.get());
      }
      // Exchange rate
      if (!pAPS450Fnc_maintain.ARAT.isAccessDISABLED()) {
         outGetHead.setY3ARAT().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.ARAT.get(), pAPS450Fnc_maintain.ARAT.getDecimals()));
      }
      // Payment terms
      if (!pAPS450Fnc_maintain.TEPY.isAccessDISABLED()) {
         outGetHead.setY3TEPY().moveLeftPad(pAPS450Fnc_maintain.TEPY.get());
      }
      // Payment mtd AP
      if (!pAPS450Fnc_maintain.PYME.isAccessDISABLED()) {
         outGetHead.setY3PYME().moveLeftPad(pAPS450Fnc_maintain.PYME.get());
      }
      // Trade code
      if (!pAPS450Fnc_maintain.TDCD.isAccessDISABLED()) {
         outGetHead.setY3TDCD().moveLeftPad(pAPS450Fnc_maintain.TDCD.get());
      }
      // Foreign currency amount
      if (!pAPS450Fnc_maintain.CUAM.isAccessDISABLED()) {
         outGetHead.setY3CUAM().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CUAM.get(), pAPS450Fnc_maintain.CUAM.getDecimals()));
      }
      // VAT amount
      if (!pAPS450Fnc_maintain.VTAM.isAccessDISABLED()) {
         outGetHead.setY3VTAM().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.VTAM.get(), pAPS450Fnc_maintain.VTAM.getDecimals()));
      }
      // Voucher number
      if (!pAPS450Fnc_maintain.VONO.isAccessDISABLED()) {
         outGetHead.setY3VONO().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.VONO.get()));
      }
      // Voucher number series
      if (!pAPS450Fnc_maintain.VSER.isAccessDISABLED()) {
         outGetHead.setY3VSER().moveLeftPad(pAPS450Fnc_maintain.VSER.get());
      }
      // Accounting date
      if (!pAPS450Fnc_maintain.ACDT.isAccessDISABLED()) {
         outGetHead.setY3ACDT().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.ACDT.get()));
      }
      // Authorizer
      if (!pAPS450Fnc_maintain.APCD.isAccessDISABLED()) {
         outGetHead.setY3APCD().moveLeftPad(pAPS450Fnc_maintain.APCD.get());
      }
      // Invoice matching
      if (!pAPS450Fnc_maintain.IMCD.isAccessDISABLED()) {
         outGetHead.setY3IMCD().moveLeftPad(pAPS450Fnc_maintain.IMCD.get());
      }
      // Service code
      if (!pAPS450Fnc_maintain.SERS.isAccessDISABLED()) {
         outGetHead.setY3SERS().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.SERS.get()));
      }
      // Due date
      if (!pAPS450Fnc_maintain.DUDT.isAccessDISABLED()) {
         outGetHead.setY3DUDT().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.DUDT.get()));
      }
      // Future exchange contract
      if (!pAPS450Fnc_maintain.FECN.isAccessDISABLED()) {
         outGetHead.setY3FECN().moveLeftPad(pAPS450Fnc_maintain.FECN.get());
      }
      // Exchange rate type
      if (!pAPS450Fnc_maintain.CRTP.isAccessDISABLED()) {
         outGetHead.setY3CRTP().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CRTP.get()));
      }
      // From/To country
      if (!pAPS450Fnc_maintain.FTCO.isAccessDISABLED()) {
         outGetHead.setY3FTCO().moveLeftPad(pAPS450Fnc_maintain.FTCO.get());
      }
      // Base country
      if (!pAPS450Fnc_maintain.BSCD.isAccessDISABLED()) {
         outGetHead.setY3BSCD().moveLeftPad(pAPS450Fnc_maintain.BSCD.get());
      }
      // Tot line amount
      if (!pAPS450Fnc_maintain.TLNA.isAccessDISABLED()) {
         outGetHead.setY3TLNA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TLNA.get(), pAPS450Fnc_maintain.TLNA.getDecimals()));
      }
      // Total charges
      if (!pAPS450Fnc_maintain.TCHG.isAccessDISABLED()) {
         outGetHead.setY3TCHG().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TCHG.get(), pAPS450Fnc_maintain.TCHG.getDecimals()));
      }
      // Total due
      if (!pAPS450Fnc_maintain.TOPA.isAccessDISABLED()) {
         outGetHead.setY3TOPA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TOPA.get(), pAPS450Fnc_maintain.TOPA.getDecimals()));
      }
      // Purchase order number
      if (!pAPS450Fnc_maintain.PUNO.isAccessDISABLED()) {
         outGetHead.setY3PUNO().moveLeftPad(pAPS450Fnc_maintain.PUNO.get());
      }
      // Order date
      if (!pAPS450Fnc_maintain.PUDT.isAccessDISABLED()) {
         outGetHead.setY3PUDT().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.PUDT.get()));
      }
      // Cash discount terms
      if (!pAPS450Fnc_maintain.TECD.isAccessDISABLED()) {
         outGetHead.setY3TECD().moveLeftPad(pAPS450Fnc_maintain.TECD.get());
      }
      // Cash discount date 1
      if (!pAPS450Fnc_maintain.CDT1.isAccessDISABLED()) {
         outGetHead.setY3CDT1().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.CDT1.get()));
      }
      // Cash discount percentage 1
      if (!pAPS450Fnc_maintain.CDP1.isAccessDISABLED()) {
         outGetHead.setY3CDP1().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CDP1.get(), pAPS450Fnc_maintain.CDP1.getDecimals()));
      }
      // Cash discount amount 1
      if (!pAPS450Fnc_maintain.CDC1.isAccessDISABLED()) {
         outGetHead.setY3CDC1().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CDC1.get(), pAPS450Fnc_maintain.CDC1.getDecimals()));
      }
      // Cash discount date 2
      if (!pAPS450Fnc_maintain.CDT2.isAccessDISABLED()) {
         outGetHead.setY3CDT2().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.CDT2.get()));
      }
      // Cash discount percentage 2
      if (!pAPS450Fnc_maintain.CDP2.isAccessDISABLED()) {
         outGetHead.setY3CDP2().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CDP2.get(), pAPS450Fnc_maintain.CDP2.getDecimals()));
      }
      // Cash discount amount 2
      if (!pAPS450Fnc_maintain.CDC2.isAccessDISABLED()) {
         outGetHead.setY3CDC2().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CDC2.get(), pAPS450Fnc_maintain.CDC2.getDecimals()));
      }
      // Cash discount date 3
      if (!pAPS450Fnc_maintain.CDT3.isAccessDISABLED()) {
         outGetHead.setY3CDT3().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.CDT3.get()));
      }
      // Cash discount percentage 3
      if (!pAPS450Fnc_maintain.CDP3.isAccessDISABLED()) {
         outGetHead.setY3CDP3().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CDP3.get(), pAPS450Fnc_maintain.CDP3.getDecimals()));
      }
      // Cash discount amount 3
      if (!pAPS450Fnc_maintain.CDC3.isAccessDISABLED()) {
         outGetHead.setY3CDC3().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.CDC3.get(), pAPS450Fnc_maintain.CDC3.getDecimals()));
      }
      // Total taxable amount
      if (!pAPS450Fnc_maintain.TTXA.isAccessDISABLED()) {
         outGetHead.setY3TTXA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TTXA.get(), pAPS450Fnc_maintain.TTXA.getDecimals()));
      }
      // Cash discount base
      if (!pAPS450Fnc_maintain.TASD.isAccessDISABLED()) {
         outGetHead.setY3TASD().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TASD.get(), pAPS450Fnc_maintain.TASD.getDecimals()));
      }
      // Pre-paid amount
      if (!pAPS450Fnc_maintain.PRPA.isAccessDISABLED()) {
         outGetHead.setY3PRPA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.PRPA.get(), pAPS450Fnc_maintain.PRPA.getDecimals()));
      }
      // VAT registration number
      if (!pAPS450Fnc_maintain.VRNO.isAccessDISABLED()) {
         outGetHead.setY3VRNO().moveLeftPad(pAPS450Fnc_maintain.VRNO.get());
      }
      // Tax applicable
      if (!pAPS450Fnc_maintain.TXAP.isAccessDISABLED()) {
         outGetHead.setY3TXAP().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TXAP.get()));
      }
      // Document code
      if (!pAPS450Fnc_maintain.DNCO.isAccessDISABLED()) {
         outGetHead.setY3DNCO().moveLeftPad(pAPS450Fnc_maintain.DNCO.get());
      }
      // Supplier acceptance
      if (!pAPS450Fnc_maintain.SUAC.isAccessDISABLED()) {
         outGetHead.setY3SUAC().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.SUAC.get()));
      }
      // Conditions for adding lines
      if (!pAPS450Fnc_maintain.SBAD.isAccessDISABLED()) {
         outGetHead.setY3SBAD().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.SBAD.get()));
      }
      // Invoice per receiving number
      if (!pAPS450Fnc_maintain.UPBI.isAccessDISABLED()) {
         outGetHead.setY3UPBI().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.UPBI.getInt()));
      }
      // AP Standard document
      if (!pAPS450Fnc_maintain.SDAP.isAccessDISABLED()) {
         outGetHead.setY3SDAP().moveLeftPad(pAPS450Fnc_maintain.SDAP.get());
      }
      // Debit note reason
      if (!pAPS450Fnc_maintain.DNRE.isAccessDISABLED()) {
         outGetHead.setY3DNRE().moveLeftPad(pAPS450Fnc_maintain.DNRE.get());
      }
      // Our invoicing address
      if (!pAPS450Fnc_maintain.PYAD.isAccessDISABLED()) {
         outGetHead.setY3PYAD().moveLeftPad(pAPS450Fnc_maintain.PYAD.get());
      }
      // Text line 1
      if (!pAPS450Fnc_maintain.SDA1.isAccessDISABLED()) {
         outGetHead.setY3SDA1().moveLeftPad(pAPS450Fnc_maintain.SDA1.get());
      }
      // Text line 2
      if (!pAPS450Fnc_maintain.SDA2.isAccessDISABLED()) {
         outGetHead.setY3SDA2().moveLeftPad(pAPS450Fnc_maintain.SDA2.get());
      }
      // Text line 3
      if (!pAPS450Fnc_maintain.SDA3.isAccessDISABLED()) {
         outGetHead.setY3SDA3().moveLeftPad(pAPS450Fnc_maintain.SDA3.get());
      }
      // EAN location code payee
      if (!pAPS450Fnc_maintain.EALP.isAccessDISABLED()) {
         outGetHead.setY3EALP().moveLeftPad(pAPS450Fnc_maintain.EALP.get());
      }
      // EAN location code consignee
      if (!pAPS450Fnc_maintain.EALR.isAccessDISABLED()) {
         outGetHead.setY3EALR().moveLeftPad(pAPS450Fnc_maintain.EALR.get());
      }
      // EAN location code supplier
      if (!pAPS450Fnc_maintain.EALS.isAccessDISABLED()) {
         outGetHead.setY3EALS().moveLeftPad(pAPS450Fnc_maintain.EALS.get());
      }
      // Delivery date
      if (!pAPS450Fnc_maintain.DEDA.isAccessDISABLED()) {
         outGetHead.setY3DEDA().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.DEDA.get()));
      }
      // Adjusted amount
      if (!pAPS450Fnc_maintain.ADAB.isAccessDISABLED()) {
         outGetHead.setY3ADAB().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.ADAB.get(), pAPS450Fnc_maintain.ADAB.getDecimals()));
      }
      // Approval date
      if (!pAPS450Fnc_maintain.AAPD.isAccessDISABLED()) {
         outGetHead.setY3AAPD().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.AAPD.get()));
      }
      // Credit number
      if (!pAPS450Fnc_maintain.CRNO.isAccessDISABLED()) {
         outGetHead.setY3CRNO().moveLeftPad(pAPS450Fnc_maintain.CRNO.get());
      }
      // Your reference
      if (!pAPS450Fnc_maintain.YRE1.isAccessDISABLED()) {
         outGetHead.setY3YRE1().moveLeftPad(pAPS450Fnc_maintain.YRE1.get());
      }
      // Reject reason
      if (!pAPS450Fnc_maintain.SCRE.isAccessDISABLED()) {
         outGetHead.setY3SCRE().moveLeftPad(pAPS450Fnc_maintain.SCRE.get());
      }
      // Reprint after adjustment
      if (!pAPS450Fnc_maintain.RPAA.isAccessDISABLED()) {
         outGetHead.setY3RPAA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.RPAA.getInt()));
      }
      // Rejection date
      if (!pAPS450Fnc_maintain.REJD.isAccessDISABLED()) {
         outGetHead.setY3REJD().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.REJD.get()));
      }
      // Entry date
      if (!pAPS450Fnc_maintain.RGDT.isAccessDISABLED()) {
         outGetHead.setY3RGDT().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.RGDT.get()));
      }
      // Change date
      if (!pAPS450Fnc_maintain.LMDT.isAccessDISABLED()) {
         outGetHead.setY3LMDT().moveLeftPad(MICommon.toAlphaDate(pAPS450Fnc_maintain.LMDT.get()));
      }
      // Bank account ID
      if (!pAPS450Fnc_maintain.BKID.isAccessDISABLED()) {
         outGetHead.setY3BKID().moveLeftPad(pAPS450Fnc_maintain.BKID.get());
      }
      // Geographical code
      if (!pAPS450Fnc_maintain.GEOC.isAccessDISABLED()) {
         outGetHead.setY3GEOC().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.GEOC.get()));
      }
      // Tax included
      if (!pAPS450Fnc_maintain.TXIN.isAccessDISABLED()) {
         outGetHead.setY3TXIN().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.TXIN.getInt()));
      }
      // Original invoice number
      if (!pAPS450Fnc_maintain.DNOI.isAccessDISABLED()) {
         outGetHead.setY3DNOI().moveLeftPad(pAPS450Fnc_maintain.DNOI.get());
      }
      // Original year
      if (!pAPS450Fnc_maintain.OYEA.isAccessDISABLED()) {
         outGetHead.setY3OYEA().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.OYEA.get()));
      }
      // Reference number
      if (!pAPS450Fnc_maintain.PPYR.isAccessDISABLED()) {
         outGetHead.setY3PPYR().moveLeftPad(pAPS450Fnc_maintain.PPYR.get());
      }
      // Payment request number
      if (!pAPS450Fnc_maintain.PPYN.isAccessDISABLED()) {
         outGetHead.setY3PPYN().moveLeftPad(pAPS450Fnc_maintain.PPYN.get());
      }
      // Year
      if (!pAPS450Fnc_maintain.YEA4.isAccessDISABLED()) {
         outGetHead.setY3YEA4().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_maintain.YEA4.get()));
      }
      // CorrelationID
      if (!pAPS450Fnc_maintain.CORI.isAccessDISABLED()) {
         outGetHead.setY3CORI().moveLeftPad(pAPS450Fnc_maintain.CORI.get());
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void getHead_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, getHead_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String getHead_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command LstLines
   */
   public void lstLines() {
      sAPS450MIRLstLines inLstLines = (sAPS450MIRLstLines)MICommon.getInDS(sAPS450MIRLstLines.class);
      sAPS450MISLstLines outLstLines = (sAPS450MISLstLines)MICommon.getOutDS(sAPS450MISLstLines.class);
      // Check fields
      // - Company
      APIBL.setCONO(LDAZD.CONO);
      // - Division
      APIBL.setDIVI().moveLeftPad(inLstLines.getQ4DIVI());
      // - Invoice batch number
      if (inLstLines.getQ4INBN().isBlank()) {
         // MSGID=WINBN02 Invoice batch number must be entered
         MICommon.setError("INBN", "WINBN02");
         return;
      }
      long INBN = 0L;
      if (MICommon.toNumeric(inLstLines.getQ4INBN())) {
         INBN = MICommon.getLong();
      } else {
         // Numeric error
         MICommon.setError("INBN");
         return;
      }
      APIBL.setINBN(INBN);
      // Transaction number
      int TRNO = 0;
      if (!inLstLines.getQ4TRNO().isBlank()) {
         if (MICommon.toNumeric(inLstLines.getQ4TRNO())) {
            TRNO = MICommon.getInt();
         } else {
            // Numeric error
            MICommon.setError("TRNO");
            return;
         }
      }
      APIBL.setTRNO(TRNO);
      // List Invoice lines (for a specific invoice - INBN)
      int noOfLines = 0;
      APIBL.SETLL("00", APIBL.getKey("00"));
      while (noOfLines < MICommon.getMaxRecords() &&
             APIBL.READE("00", APIBL.getKey("00", 3)))
      {
         noOfLines++;
         // Call APS451Fnc, maintain - mode RETRIEVE, step INITIATE
         // =========================================
         pAPS451Fnc_maintain = get_pAPS451Fnc_maintain();
         pAPS451Fnc_maintain.messages.forgetNotifications();
         pAPS451Fnc_maintain.prepare(cEnumMode.RETRIEVE, cEnumStep.INITIATE);
         // Pass a reference to the record.
         pAPS451Fnc_maintain.APIBL = APIBL;
         pAPS451Fnc_maintain.passFAPIBL = true;
         passExtensionTable(); // exit point for modification
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS451Fnc", pAPS451Fnc_maintain);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         lstLines_handleMessages(pAPS451Fnc_maintain.messages);
         // Release resources allocated by the parameter list.
         pAPS451Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // Return data
         // =========================================
         lstLines_setOutData(outLstLines);
         MICommon.setData(outLstLines.get());
         MICommon.reply();
      }
      // Empty reply buffer
      MICommon.clearBuffer();
   }

   /**
   * Set out data for command LstLines.
   * @param outLstLines
   *    DS for out data.
   */
   public void lstLines_setOutData(sAPS450MISLstLines outLstLines) {
      outLstLines.clear();
      // Transaction number
      if (!pAPS451Fnc_maintain.TRNO.isAccessDISABLED()) {
         outLstLines.setY4TRNO().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.TRNO.get()));
      }
      // Line type
      if (!pAPS451Fnc_maintain.RDTP.isAccessDISABLED()) {
         outLstLines.setY4RDTP().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.RDTP.get()));
      }
      // Invoice batch line errors
      if (!pAPS451Fnc_maintain.IBLE.isAccessDISABLED()) {
         outLstLines.setY4IBLE().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.IBLE.get()));
      }
      // Service code
      if (!pAPS451Fnc_maintain.SERS.isAccessDISABLED()) {
         outLstLines.setY4SERS().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.SERS.get()));
      }
      // Net amount
      if (!pAPS451Fnc_maintain.NLAM.isAccessDISABLED()) {
         outLstLines.setY4NLAM().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.NLAM.get(), pAPS451Fnc_maintain.NLAM.getDecimals()));
      }
      // Adjusted amount
      if (!pAPS451Fnc_maintain.ADAB.isAccessDISABLED()) {
         outLstLines.setY4ADAB().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.ADAB.get(), pAPS451Fnc_maintain.ADAB.getDecimals()));
      }
      // Currency
      if (!pAPS451Fnc_maintain.CUCD.isAccessDISABLED()) {
         outLstLines.setY4CUCD().moveLeftPad(pAPS451Fnc_maintain.CUCD.get());
      }
      // VAT amount 1
      if (!pAPS451Fnc_maintain.VTA1.isAccessDISABLED()) {
         outLstLines.setY4VTA1().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.VTA1.get(), pAPS451Fnc_maintain.VTA1.getDecimals()));
      }
      // VAT amount 2
      if (!pAPS451Fnc_maintain.VTA2.isAccessDISABLED()) {
         outLstLines.setY4VTA2().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.VTA2.get(), pAPS451Fnc_maintain.VTA2.getDecimals()));
      }
      // VAT rate 1
      if (!pAPS451Fnc_maintain.VTP1.isAccessDISABLED()) {
         outLstLines.setY4VTP1().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.VTP1.get(), pAPS451Fnc_maintain.VTP1.getDecimals()));
      }
      // VAT rate 2
      if (!pAPS451Fnc_maintain.VTP2.isAccessDISABLED()) {
         outLstLines.setY4VTP2().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.VTP2.get(), pAPS451Fnc_maintain.VTP2.getDecimals()));
      }
      // VAT code
      if (!pAPS451Fnc_maintain.VTCD.isAccessDISABLED()) {
         outLstLines.setY4VTCD().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.VTCD.get()));
      }
      // Purchase order number
      if (!pAPS451Fnc_maintain.PUNO.isAccessDISABLED()) {
         outLstLines.setY4PUNO().moveLeftPad(pAPS451Fnc_maintain.PUNO.get());
      }
      // PO line
      if (!pAPS451Fnc_maintain.PNLI.isAccessDISABLED()) {
         outLstLines.setY4PNLI().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.PNLI.get()));
      }
      // PO line sub number
      if (!pAPS451Fnc_maintain.PNLS.isAccessDISABLED()) {
         outLstLines.setY4PNLS().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.PNLS.get()));
      }
      // Invoiced qty
      if (!pAPS451Fnc_maintain.IVQA.isAccessDISABLED()) {
         outLstLines.setY4IVQA().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.IVQA.get(), pAPS451Fnc_maintain.IVQA.getDecimals()));
      }
      // U/M (Invoiced qty)
      if (!pAPS451Fnc_maintain.PUUN.isAccessDISABLED()) {
         outLstLines.setY4PUUN().moveLeftPad(pAPS451Fnc_maintain.PUUN.get());
      }
      // Gross price
      if (!pAPS451Fnc_maintain.GRPR.isAccessDISABLED()) {
         outLstLines.setY4GRPR().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.GRPR.get(), pAPS451Fnc_maintain.GRPR.getDecimals()));
      }
      // U/M (Gross price)
      if (!pAPS451Fnc_maintain.PPUN.isAccessDISABLED()) {
         outLstLines.setY4PPUN().moveLeftPad(pAPS451Fnc_maintain.PPUN.get());
      }
      // Net price
      if (!pAPS451Fnc_maintain.NEPR.isAccessDISABLED()) {
         outLstLines.setY4NEPR().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.NEPR.get(), pAPS451Fnc_maintain.NEPR.getDecimals()));
      }
      // Purchase price qty
      if (!pAPS451Fnc_maintain.PUCD.isAccessDISABLED()) {
         outLstLines.setY4PUCD().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.PUCD.get()));
      }
      // Gross amount
      if (!pAPS451Fnc_maintain.GLAM.isAccessDISABLED()) {
         outLstLines.setY4GLAM().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.GLAM.get(), pAPS451Fnc_maintain.GLAM.getDecimals()));
      }
      // Discount
      if (!pAPS451Fnc_maintain.DIPC.isAccessDISABLED()) {
         outLstLines.setY4DIPC().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.DIPC.get(), pAPS451Fnc_maintain.DIPC.getDecimals()));
      }
      // Discount amount
      if (!pAPS451Fnc_maintain.DIAM.isAccessDISABLED()) {
         outLstLines.setY4DIAM().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.DIAM.get(), pAPS451Fnc_maintain.DIAM.getDecimals()));
      }
      // Invoiced catch weight
      if (!pAPS451Fnc_maintain.IVCW.isAccessDISABLED()) {
         outLstLines.setY4IVCW().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.IVCW.get(), pAPS451Fnc_maintain.IVCW.getDecimals()));
      }
      // Item number
      if (!pAPS451Fnc_maintain.ITNO.isAccessDISABLED()) {
         outLstLines.setY4ITNO().moveLeftPad(pAPS451Fnc_maintain.ITNO.get());
      }
      // Alias number
      if (!pAPS451Fnc_maintain.POPN.isAccessDISABLED()) {
         outLstLines.setY4POPN().moveLeftPad(pAPS451Fnc_maintain.POPN.get());
      }
      // Self billing agreement number
      if (!pAPS451Fnc_maintain.SBAN.isAccessDISABLED()) {
         outLstLines.setY4SBAN().moveLeftPad(pAPS451Fnc_maintain.SBAN.get());
      }
      // Sequence no
      if (!pAPS451Fnc_maintain.CDSE.isAccessDISABLED()) {
         outLstLines.setY4CDSE().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.CDSE.get()));
      }
      // Costing element
      if (!pAPS451Fnc_maintain.CEID.isAccessDISABLED()) {
         outLstLines.setY4CEID().moveLeftPad(pAPS451Fnc_maintain.CEID.get());
      }
      // Receiving number
      if (!pAPS451Fnc_maintain.REPN.isAccessDISABLED()) {
         outLstLines.setY4REPN().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.REPN.get()));
      }
      // Receipt type
      if (!pAPS451Fnc_maintain.RELP.isAccessDISABLED()) {
         outLstLines.setY4RELP().moveLeftPad(MICommon.toAlpha(pAPS451Fnc_maintain.RELP.get()));
      }
      // Deliver note number
      if (!pAPS451Fnc_maintain.SUDO.isAccessDISABLED()) {
         outLstLines.setY4SUDO().moveLeftPad(pAPS451Fnc_maintain.SUDO.get());
      }
      // Deliver note date
      if (!pAPS451Fnc_maintain.DNDT.isAccessDISABLED()) {
         outLstLines.setY4DNDT().moveLeftPad(MICommon.toAlphaDate(pAPS451Fnc_maintain.DNDT.get()));
      }
      // Entry date
      if (!pAPS451Fnc_maintain.RGDT.isAccessDISABLED()) {
         outLstLines.setY4RGDT().moveLeftPad(MICommon.toAlphaDate(pAPS451Fnc_maintain.RGDT.get()));
      }
      // Change date
      if (!pAPS451Fnc_maintain.LMDT.isAccessDISABLED()) {
         outLstLines.setY4LMDT().moveLeftPad(MICommon.toAlphaDate(pAPS451Fnc_maintain.LMDT.get()));
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void lstLines_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, lstLines_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String lstLines_mapFLDI(String FLDI) {
      return FLDI;
   }
   
   /**
   * Execute command Acknowledge.
   */
   public void acknowledge() {
      sAPS450MIRAcknowledge inAcknowledge = (sAPS450MIRAcknowledge)MICommon.getInDS(sAPS450MIRAcknowledge.class);
      boolean newEntryContext = false;
      // Call APS450Fnc, acknowledge - step INITIATE
      // =========================================
      pAPS450Fnc_acknowledge = get_pAPS450Fnc_acknowledge();
      pAPS450Fnc_acknowledge.messages.forgetNotifications();
      pAPS450Fnc_acknowledge.prepare(cEnumStep.INITIATE);
      pAPS450Fnc_acknowledge.indicateAutomated();
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS450Fnc_acknowledge.DIVI, inAcknowledge.getQADIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS450Fnc_acknowledge.INBN, inAcknowledge.getQAINBN(), "INBN")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_acknowledge);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      acknowledge_handleMessages(pAPS450Fnc_acknowledge.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_acknowledge.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, acknowledge - step VALIDATE
      // =========================================
      do {
         pAPS450Fnc_acknowledge.prepare(cEnumStep.VALIDATE);
         // Move fields to function parameters
         acknowledge_getInData(inAcknowledge);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_acknowledge);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         acknowledge_handleMessages(pAPS450Fnc_acknowledge.messages);
         newEntryContext = pAPS450Fnc_acknowledge.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS450Fnc_acknowledge.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      acknowledge_checkIfInvalidFieldsSet(inAcknowledge);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, acknowledge - step UPDATE
      // =========================================
      pAPS450Fnc_acknowledge.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS450Fnc_acknowledge.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS450Fnc_acknowledge.messages, transStatus);
      // =========================================
      // Handle messages
      acknowledge_handleMessages(pAPS450Fnc_acknowledge.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_acknowledge.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
   }

   /**
   * Get in data for command Acknowledge.
   * @param inAcknowledge
   *    DS for in data.
   */
   public void acknowledge_getInData(sAPS450MIRAcknowledge inAcknowledge) {
      // Credit number
      if (!MICommon.set(pAPS450Fnc_acknowledge.CRNO, inAcknowledge.getQACRNO())) {
         return;
      }
      // Your reference
      if (!MICommon.set(pAPS450Fnc_acknowledge.YRE1, inAcknowledge.getQAYRE1())) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command Acknowledge.
   * @param inAcknowledge
   *    DS for in data.
   */
   public void acknowledge_checkIfInvalidFieldsSet(sAPS450MIRAcknowledge inAcknowledge) {
      // Credit number
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_acknowledge.CRNO, inAcknowledge.getQACRNO(), "CRNO")) {
         return;
      }
      // Your reference
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_acknowledge.YRE1, inAcknowledge.getQAYRE1(), "YRE1")) {
         return;
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void acknowledge_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, acknowledge_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String acknowledge_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command ApproveInvoice.
   */
   public void approveInvoice() {
      sAPS450MIRApproveInvoice inApproveInvoice = (sAPS450MIRApproveInvoice)MICommon.getInDS(sAPS450MIRApproveInvoice.class);
      boolean newEntryContext = false;
      // Call APS450Fnc, approveInvoice - step INITIATE
      // =========================================
      pAPS450Fnc_approveInvoice = get_pAPS450Fnc_approveInvoice();
      pAPS450Fnc_approveInvoice.messages.forgetNotifications();
      pAPS450Fnc_approveInvoice.prepare(cEnumStep.INITIATE);
      pAPS450Fnc_approveInvoice.indicateAutomated();
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS450Fnc_approveInvoice.DIVI, inApproveInvoice.getQ5DIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS450Fnc_approveInvoice.INBN, inApproveInvoice.getQ5INBN(), "INBN")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_approveInvoice);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      approveInvoice_handleMessages(pAPS450Fnc_approveInvoice.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_approveInvoice.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, approveInvoice - step VALIDATE
      // =========================================
      do {
         pAPS450Fnc_approveInvoice.prepare(cEnumStep.VALIDATE);
         // Move fields to function parameters
         approveInvoice_getInData(inApproveInvoice);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_approveInvoice);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         approveInvoice_handleMessages(pAPS450Fnc_approveInvoice.messages);
         newEntryContext = pAPS450Fnc_approveInvoice.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS450Fnc_approveInvoice.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      approveInvoice_checkIfInvalidFieldsSet(inApproveInvoice);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, approveInvoice - step UPDATE
      // =========================================
      pAPS450Fnc_approveInvoice.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS450Fnc_approveInvoice.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS450Fnc_approveInvoice.messages, transStatus);
      // =========================================
      // Handle messages
      approveInvoice_handleMessages(pAPS450Fnc_approveInvoice.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_approveInvoice.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
   }

   /**
   * Get in data for command ApproveInvoice.
   * @param inApproveInvoice
   *    DS for in data.
   */
   public void approveInvoice_getInData(sAPS450MIRApproveInvoice inApproveInvoice) {
      // Approval date
      if (!MICommon.setDate(pAPS450Fnc_approveInvoice.AAPD, inApproveInvoice.getQ5AAPD(), "AAPD")) {
         return;
      }
      // Credit number
      if (!MICommon.set(pAPS450Fnc_approveInvoice.CRNO, inApproveInvoice.getQ5CRNO())) {
         return;
      }
      // Your reference
      if (!MICommon.set(pAPS450Fnc_approveInvoice.YRE1, inApproveInvoice.getQ5YRE1())) {
         return;
      }
      // Supplier invoice number
      if (!MICommon.set(pAPS450Fnc_approveInvoice.SINO, inApproveInvoice.getQ5SINO())) {
         return;
      }
      // Invoice date
      if (!MICommon.setDate(pAPS450Fnc_approveInvoice.IVDT, inApproveInvoice.getQ5IVDT(), "IVDT")) {
         return;
      }
      // Due date
      if (!MICommon.setDate(pAPS450Fnc_approveInvoice.DUDT, inApproveInvoice.getQ5DUDT(), "DUDT")) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command ApproveInvoice.
   * @param inApproveInvoice
   *    DS for in data.
   */
   public void approveInvoice_checkIfInvalidFieldsSet(sAPS450MIRApproveInvoice inApproveInvoice) {
      // Approval date
      if (MICommon.checkIfDateNotAllowed(pAPS450Fnc_approveInvoice.AAPD, inApproveInvoice.getQ5AAPD(), "AAPD")) {
         return;
      }
      // Credit number
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_approveInvoice.CRNO, inApproveInvoice.getQ5CRNO(), "CRNO")) {
         return;
      }
      // Your reference
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_approveInvoice.YRE1, inApproveInvoice.getQ5YRE1(), "YRE1")) {
         return;
      }
      // Supplier invoice number
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_approveInvoice.SINO, inApproveInvoice.getQ5SINO(), "SINO")) {
         return;
      }
      // Invoice date
      if (MICommon.checkIfDateNotAllowed(pAPS450Fnc_approveInvoice.IVDT, inApproveInvoice.getQ5IVDT(), "IVDT")) {
         return;
      }
      // Due date
      if (MICommon.checkIfDateNotAllowed(pAPS450Fnc_approveInvoice.DUDT, inApproveInvoice.getQ5DUDT(), "DUDT")) {
         return;
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void approveInvoice_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, approveInvoice_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String approveInvoice_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command RejectInvoice.
   */
   public void rejectInvoice() {
      sAPS450MIRRejectInvoice inRejectInvoice = (sAPS450MIRRejectInvoice)MICommon.getInDS(sAPS450MIRRejectInvoice.class);
      sAPS450MISRejectInvoice outRejectInvoice = (sAPS450MISRejectInvoice)MICommon.getOutDS(sAPS450MISRejectInvoice.class);
      boolean newEntryContext = false;
      // Call APS450Fnc, rejectInvoice - step INITIATE
      // =========================================
      pAPS450Fnc_rejectInvoice = get_pAPS450Fnc_rejectInvoice();
      pAPS450Fnc_rejectInvoice.messages.forgetNotifications();
      pAPS450Fnc_rejectInvoice.prepare(cEnumStep.INITIATE);
      pAPS450Fnc_rejectInvoice.indicateAutomated();
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.DIVI, inRejectInvoice.getQ6DIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.INBN, inRejectInvoice.getQ6INBN(), "INBN")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_rejectInvoice);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      rejectInvoice_handleMessages(pAPS450Fnc_rejectInvoice.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_rejectInvoice.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, rejectInvoice - step VALIDATE
      // =========================================
      do {
         pAPS450Fnc_rejectInvoice.prepare(cEnumStep.VALIDATE);
         // Move fields to function parameters
         rejectInvoice_getInData(inRejectInvoice);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_rejectInvoice);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         rejectInvoice_handleMessages(pAPS450Fnc_rejectInvoice.messages);
         newEntryContext = pAPS450Fnc_rejectInvoice.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS450Fnc_rejectInvoice.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      rejectInvoice_checkIfInvalidFieldsSet(inRejectInvoice);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS450Fnc, rejectInvoice - step UPDATE
      // =========================================
      pAPS450Fnc_rejectInvoice.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS450Fnc_rejectInvoice.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS450Fnc_rejectInvoice.messages, transStatus);
      // =========================================
      // Handle messages
      rejectInvoice_handleMessages(pAPS450Fnc_rejectInvoice.messages);
      // Release resources allocated by the parameter list.
      pAPS450Fnc_rejectInvoice.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Return data
      // =========================================
      rejectInvoice_setOutData(outRejectInvoice);
      MICommon.setData(outRejectInvoice.get());
   }

   /**
   * Get in data for command RejectInvoice.
   * @param inRejectInvoice
   *    DS for in data.
   */
   public void rejectInvoice_getInData(sAPS450MIRRejectInvoice inRejectInvoice) {
      // Rejection date
      if (!MICommon.setDate(pAPS450Fnc_rejectInvoice.REJD, inRejectInvoice.getQ6REJD(), "REJD")) {
         return;
      }
      // Reject Reason
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.SCRE, inRejectInvoice.getQ6SCRE())) {
         return;
      }
      // Reprint after adjustment
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.RPAA, inRejectInvoice.getQ6RPAA(), "RPAA")) {
         return;
      }
      // Text line 1
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.SDA1, inRejectInvoice.getQ6SDA1())) {
         return;
      }
      // Text line 2
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.SDA2, inRejectInvoice.getQ6SDA2())) {
         return;
      }
      // Text line 3
      if (!MICommon.set(pAPS450Fnc_rejectInvoice.SDA3, inRejectInvoice.getQ6SDA3())) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command RejectInvoice.
   * @param inRejectInvoice
   *    DS for in data.
   */
   public void rejectInvoice_checkIfInvalidFieldsSet(sAPS450MIRRejectInvoice inRejectInvoice) {
      // Rejection date
      if (MICommon.checkIfDateNotAllowed(pAPS450Fnc_rejectInvoice.REJD, inRejectInvoice.getQ6REJD(), "REJD")) {
         return;
      }
      // Reject Reason
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_rejectInvoice.SCRE, inRejectInvoice.getQ6SCRE(), "SCRE")) {
         return;
      }
      // Reprint after adjustment
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_rejectInvoice.RPAA, inRejectInvoice.getQ6RPAA(), "RPAA")) {
         return;
      }
      // Text line 1
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_rejectInvoice.SDA1, inRejectInvoice.getQ6SDA1(), "SDA1")) {
         return;
      }
      // Text line 2
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_rejectInvoice.SDA2, inRejectInvoice.getQ6SDA2(), "SDA2")) {
         return;
      }
      // Text line 3
      if (MICommon.checkIfNotAllowed(pAPS450Fnc_rejectInvoice.SDA3, inRejectInvoice.getQ6SDA3(), "SDA3")) {
         return;
      }
   }

   /**
   * Set out data for command RejectInvoice.
   * @param outRejectInvoice
   *    DS for out data.
   */
   public void rejectInvoice_setOutData(sAPS450MISRejectInvoice outRejectInvoice) {
      outRejectInvoice.clear();
      // Invoice Batch number
      if (!pAPS450Fnc_rejectInvoice.INBN.isAccessDISABLED()) {
         outRejectInvoice.setY6INBN().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_rejectInvoice.INBN.get()));
      }
      // Transaction number in rejection history
      if (!pAPS450Fnc_rejectInvoice.TRNO.isAccessDISABLED()) {
         outRejectInvoice.setY6TRNO().moveLeftPad(MICommon.toAlpha(pAPS450Fnc_rejectInvoice.TRNO.get()));
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void rejectInvoice_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, rejectInvoice_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String rejectInvoice_mapFLDI(String FLDI) {
      return FLDI;
   }

   /**
   * Execute command AdjustLine.
   */
   public void adjustLine() {
      sAPS450MIRAdjustLine inAdjustLine = (sAPS450MIRAdjustLine)MICommon.getInDS(sAPS450MIRAdjustLine.class);
      boolean newEntryContext = false;
      // Call APS451Fnc, adjustLine - step INITIATE
      // =========================================
      pAPS451Fnc_adjustLine = get_pAPS451Fnc_adjustLine();
      pAPS451Fnc_adjustLine.messages.forgetNotifications();
      pAPS451Fnc_adjustLine.prepare(cEnumStep.INITIATE);
      pAPS451Fnc_adjustLine.indicateAutomated();
      // Set key parameters
      // - Division
      if (!MICommon.set(pAPS451Fnc_adjustLine.DIVI, inAdjustLine.getQCDIVI())) {
         return;
      }
      // - Invoice batch number
      if (!MICommon.set(pAPS451Fnc_adjustLine.INBN, inAdjustLine.getQCINBN(), "INBN")) {
         return;
      }
      // - Transaction number - i.e. invoice line
      if (!MICommon.set(pAPS451Fnc_adjustLine.TRNO, inAdjustLine.getQCTRNO(), "TRNO")) {
         return;
      }
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAPS451Fnc_adjustLine);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      adjustLine_handleMessages(pAPS451Fnc_adjustLine.messages);
      // Release resources allocated by the parameter list.
      pAPS451Fnc_adjustLine.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS451Fnc, adjustLine - step VALIDATE
      // =========================================
      do {
         pAPS451Fnc_adjustLine.prepare(cEnumStep.VALIDATE);
         // Move fields to function parameters
         adjustLine_getInData(inAdjustLine);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS451Fnc", pAPS451Fnc_adjustLine);
         LDAZZ.FPNM.move(XFPGNM);
         // =========================================
         // Handle messages
         adjustLine_handleMessages(pAPS451Fnc_adjustLine.messages);
         newEntryContext = pAPS451Fnc_adjustLine.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS451Fnc_adjustLine.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      adjustLine_checkIfInvalidFieldsSet(inAdjustLine);
      // Return error message
      if (MICommon.isError()) {
         return;
      }
      // Call APS451Fnc, adjustLine - step UPDATE
      // =========================================
      pAPS451Fnc_adjustLine.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS451Fnc_adjustLine.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS451Fnc_adjustLine.messages, transStatus);
      // =========================================
      // Handle messages
      adjustLine_handleMessages(pAPS451Fnc_adjustLine.messages);
      // Release resources allocated by the parameter list.
      pAPS451Fnc_adjustLine.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
   }

   /**
   * Get in data for command AdjustLine.
   * @param inAdjustLine
   *    DS for in data.
   */
   public void adjustLine_getInData(sAPS450MIRAdjustLine inAdjustLine) {
      // Adjusted amount
      if (!MICommon.set(pAPS451Fnc_adjustLine.ADAB, inAdjustLine.getQCADAB(), "ADAB")) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command AdjustLine.
   * @param inAdjustLine
   *    DS for in data.
   */
   public void adjustLine_checkIfInvalidFieldsSet(sAPS450MIRAdjustLine inAdjustLine) {
      // Adjusted amount
      if (MICommon.checkIfNotAllowed(pAPS451Fnc_adjustLine.ADAB, inAdjustLine.getQCADAB(), "ADAB")) {
         return;
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void adjustLine_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, adjustLine_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
            return;
         }
      }
   }

   /**
   * Modify this method if the FLDI is different in the MI transaction than in the 
   * function program.
   * @param FLDI
   *    Field ID from the function program.
   * @return
   *    Field ID used in the MI transaction.
   */
   public String adjustLine_mapFLDI(String FLDI) {
      return FLDI;
   }
   
   /**
   * Execute command LstClaimDetails
   */
   public void lstClaimDetails() {
      sAPS450MIRLstClaimDetails inLstClaimDetails = (sAPS450MIRLstClaimDetails)MICommon.getInDS(sAPS450MIRLstClaimDetails.class);
      sAPS450MISLstClaimDetails outLstClaimDetails = (sAPS450MISLstClaimDetails)MICommon.getOutDS(sAPS450MISLstClaimDetails.class);
      // Check fields
      // - Company
      PSUPR.setCONO(LDAZD.CONO);
      // - Division
      if (!CRCommon.validateDIVIfinancialAccessAuthority(inLstClaimDetails.getQ7DIVI(), PLCHKAD, MSGID, MSGDTA)) {
         MICommon.setError("DIVI", MSGID.toStringRTrim(), MSGDTA);
         return;
      }
      PSUPR.setDIVI().moveLeftPad(inLstClaimDetails.getQ7DIVI());
      // - Invoice batch number
      if (inLstClaimDetails.getQ7INBN().isBlank()) {
         // MSGID=WINBN02 Invoice batch number must be entered
         MICommon.setError("INBN", "WINBN02");
         return;
      }
      long INBN = 0L;
      if (MICommon.toNumeric(inLstClaimDetails.getQ7INBN())) {
         INBN = MICommon.getLong();
      } else {
         // Numeric error
         MICommon.setError("INBN");
         return;
      }
      if (INBN == 0L) {
         // MSGID=WINBN02 Invoice batch number must be entered
         MICommon.setError("INBN", "WINBN02");
         return;
      }
      PSUPR.setINBN(INBN);
      // - Year
      int YEA4 = 0;
      if (!inLstClaimDetails.getQ7YEA4().isBlank()) {
         if (MICommon.toNumeric(inLstClaimDetails.getQ7YEA4())) {
            YEA4 = MICommon.getInt();
         } else {
            // Numeric error
            MICommon.setError("YEA4");
            return;
         }
      }
      PSUPR.setYEA4(YEA4);
      // - Extended Invoice Number
      PSUPR.setEXIN().moveLeftPad(inLstClaimDetails.getQ7EXIN()); 
      
      // -Order category
      PSUPR.setORCA().moveLeftPad(inLstClaimDetails.getQ7ORCA()); 
      if(PSUPR.getORCA().EQ("311")){
         PSUPR.setALI1(0);
         // - Delivery number
         int RIDI = 0;
         if (!inLstClaimDetails.getQ7RIDI().isBlank()) {
            if (MICommon.toNumeric(inLstClaimDetails.getQ7RIDI())) {
               RIDI = MICommon.getInt();
            } else {
               // Numeric error
               MICommon.setError("RIDI");
               return;
            }
         }
         PSUPR.setRIDI(RIDI);
      }
      if(PSUPR.getORCA().EQ("771")){
         PSUPR.setRIDI(0);
         // - Transaction number
         int ALI1 = 0;
         if (!inLstClaimDetails.getQ7ALI1().isBlank()) {
            if (MICommon.toNumeric(inLstClaimDetails.getQ7ALI1())) {
               ALI1 = MICommon.getInt();
            } else {
               // Numeric error
               MICommon.setError("ALI1");
               return;
            }
         }
         PSUPR.setALI1(ALI1);
      }
      // - Customer order number
      PSUPR.setRIDN().moveLeftPad(inLstClaimDetails.getQ7RIDN()); 
      // - Line number
      int RIDL = 0;
      if (!inLstClaimDetails.getQ7RIDL().isBlank()) {
         if (MICommon.toNumeric(inLstClaimDetails.getQ7RIDL())) {
            RIDL = MICommon.getInt();
         } else {
            // Numeric error
            MICommon.setError("RIDL");
            return;
         }
      }
      PSUPR.setRIDL(RIDL);
      // - Line suffix
      int RIDX = 0;
      if (!inLstClaimDetails.getQ7RIDX().isBlank()) {
         if (MICommon.toNumeric(inLstClaimDetails.getQ7RIDX())) {
            RIDX = MICommon.getInt();
         } else {
            // Numeric error
            MICommon.setError("RIDX");
            return;
         }
      }
      PSUPR.setRIDX(RIDX);
      // Get decimals - local currency
      found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, false, LDAZD.CONO, PSUPR.getDIVI());
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, false, LDAZD.CONO, MNDIV.getLOCD());
      cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
      int localDecimals = DSCUCD.getYQDCCD();
      // List claim invoice lines
      found_MITMAS = false; // ensure fresh reading of MITMAS
      int noOfLines = 0;
      PSUPR.SETLL("30", PSUPR.getKey("30"));
      while (noOfLines < MICommon.getMaxRecords() &&
             PSUPR.READE("30", PSUPR.getKey("30",  3)))
      {
         noOfLines++;
         // Return data
         LstClaimDetails_setOutData(outLstClaimDetails, localDecimals);
         MICommon.setData(outLstClaimDetails.get());
         MICommon.reply();
      }
      // Empty reply buffer
      MICommon.clearBuffer();
   }

   /**
   * Set out data for command LstLines.
   * @param outLstLines
   *    DS for out data.
   * @param localDecimals
   *    The number of decimals for the local currency in the selected division
   */
   public void LstClaimDetails_setOutData(sAPS450MISLstClaimDetails outLstClaimDetails, int localDecimals) {
      outLstClaimDetails.clear();
      // Get decimals - foreing currency
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, LDAZD.CONO, PSUPR.getCUCD());
      cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
      int currencyDecimals = DSCUCD.getYQDCCD();
      // Get item
      found_MITMAS = cRefITNOext.getMITMAS(ITMAS, found_MITMAS, LDAZD.CONO, PSUPR.getITNO());
      // Set output fields:
      // Year
      outLstClaimDetails.setY7YEA4().moveLeftPad(MICommon.toAlpha(PSUPR.getYEA4())); 
      // Extended Invoice Number
      outLstClaimDetails.setY7EXIN().moveLeftPad(PSUPR.getEXIN()); 
      // Delivery number
      outLstClaimDetails.setY7RIDI().moveLeftPad(MICommon.toAlpha(PSUPR.getRIDI())); 
      // Order category
      outLstClaimDetails.setY7ORCA().moveLeftPad(PSUPR.getORCA());
      // Customer order number
      outLstClaimDetails.setY7RIDN().moveLeftPad(PSUPR.getRIDN()); 
      // Line number
      outLstClaimDetails.setY7RIDL().moveLeftPad(MICommon.toAlpha(PSUPR.getRIDL())); 
      // Line suffix
      outLstClaimDetails.setY7RIDX().moveLeftPad(MICommon.toAlpha(PSUPR.getRIDX())); 
      // Supplier
      outLstClaimDetails.setY7SUNO().moveLeftPad(PSUPR.getSUNO()); 
      // Supplier name
      outLstClaimDetails.setY7SUNM().moveLeftPad(PSUPR.getSUNM()); 
      // Payee
      outLstClaimDetails.setY7SPYN().moveLeftPad(PSUPR.getSPYN()); 
      // Facility
      outLstClaimDetails.setY7FACI().moveLeftPad(PSUPR.getFACI()); 
      // Supplier claim reference type
      outLstClaimDetails.setY7CLAT().moveLeftPad(PSUPR.getCLAT()); 
      // Supplier claim reference number
      outLstClaimDetails.setY7CLAR().moveLeftPad(PSUPR.getCLAR()); 
      // Customer
      outLstClaimDetails.setY7CUNO().moveLeftPad(PSUPR.getCUNO()); 
      // Name
      outLstClaimDetails.setY7CUNM().moveLeftPad(PSUPR.getCUNM()); 
      // Warehouse
      outLstClaimDetails.setY7WHLO().moveLeftPad(PSUPR.getWHLO()); 
      // Project number
      outLstClaimDetails.setY7PROJ().moveLeftPad(PSUPR.getPROJ()); 
      // Project element
      outLstClaimDetails.setY7ELNO().moveLeftPad(PSUPR.getELNO()); 
      // Invoice date
      outLstClaimDetails.setY7IDAT().moveLeftPad(MICommon.toAlphaDate(PSUPR.getIDAT())); 
      // Invoiced quantity - basic U/M
      outLstClaimDetails.setY7IVQT().moveLeftPad(MICommon.toAlpha(PSUPR.getIVQT(), ITMAS.getDCCD())); 
      // Basic unit of measure
      outLstClaimDetails.setY7UNMS().moveLeftPad(PSUPR.getUNMS()); 
      // Invoiced quantity - alternate U/M
      outLstClaimDetails.setY7IVQA().moveLeftPad(MICommon.toAlpha(PSUPR.getIVQA(), ITMAS.getDCCD())); 
      // Alternate U/M
      outLstClaimDetails.setY7ALUN().moveLeftPad(PSUPR.getALUN()); 
      // Invoiced quantity - sales price U/M
      outLstClaimDetails.setY7IVQS().moveLeftPad(MICommon.toAlpha(PSUPR.getIVQS(), ITMAS.getDCCD())); 
      // Sales price unit of measure
      outLstClaimDetails.setY7SPUN().moveLeftPad(PSUPR.getSPUN()); 
      // Invoice amount - local currency
      outLstClaimDetails.setY7IVLA().moveLeftPad(MICommon.toAlpha(PSUPR.getIVLA(), localDecimals)); 
      // VAT code - purchase
      outLstClaimDetails.setY7VTCP().moveLeftPad(MICommon.toAlpha(PSUPR.getVTCP())); 
      // Item number
      outLstClaimDetails.setY7ITNO().moveLeftPad(PSUPR.getITNO()); 
      // Name
      outLstClaimDetails.setY7ITDS().moveLeftPad(PSUPR.getITDS()); 
      // Supplier item number
      outLstClaimDetails.setY7SITE().moveLeftPad(PSUPR.getSITE()); 
      // Supplier claim amount
      outLstClaimDetails.setY7SCAM().moveLeftPad(MICommon.toAlpha(PSUPR.getSCAM(), currencyDecimals)); 
      // Currency
      outLstClaimDetails.setY7CUCD().moveLeftPad(PSUPR.getCUCD()); 
      // Exchange rate type
      outLstClaimDetails.setY7CRTP().moveLeftPad(MICommon.toAlpha(PSUPR.getCRTP())); 
      // Exchange rate
      outLstClaimDetails.setY7ARAT().moveLeftPad(MICommon.toAlpha(PSUPR.getARAT(), 6)); 
      // Customer order type
      outLstClaimDetails.setY7ORTP().moveLeftPad(PSUPR.getORTP()); 
      // Supplier claim transaction status
      outLstClaimDetails.setY7SCTS().moveLeftPad(PSUPR.getSCTS()); 
      // Claim matching date
      outLstClaimDetails.setY7CLDT().moveLeftPad(MICommon.toAlphaDate(PSUPR.getCLDT())); 
      // Type of claim
      outLstClaimDetails.setY7CLTY().moveLeftPad(MICommon.toAlpha(PSUPR.getCLTY()));
      // Kit number
      outLstClaimDetails.setY7KTNO().moveLeftPad(PSUPR.getKTNO()); 
      // Ordered quantity - alternate U/M
      outLstClaimDetails.setY7ORQA().moveLeftPad(MICommon.toAlpha(PSUPR.getORQA(), ITMAS.getDCCD())); 
      // Ordered quantity - basic U/M
      outLstClaimDetails.setY7ORQT().moveLeftPad(MICommon.toAlpha(PSUPR.getORQT(), ITMAS.getDCCD())); 
      // Pending claim amount
      outLstClaimDetails.setY7DPCL().moveLeftPad(MICommon.toAlpha(PSUPR.getDPCL(), localDecimals)); 
      // Transaction number
      outLstClaimDetails.setY7ALI1().moveLeftPad(MICommon.toAlpha(PSUPR.getALI1())); 
      // Limit value 
      outLstClaimDetails.setY7LIMT().moveLeftPad(MICommon.toAlpha(PSUPR.getLIMT(), 2)); 
      // Rebate percentage
      outLstClaimDetails.setY7SREP().moveLeftPad(MICommon.toAlpha(PSUPR.getSREP(), 6)); 
      // Agreement reference number
      outLstClaimDetails.setY7RASR().moveLeftPad(PSUPR.getRASR()); 
      // Credit indicator
      outLstClaimDetails.setY7EICI().moveLeftPad(PSUPR.getEICI()); 
   }

  /**
   *    SETLR - End of program
   */
   public void SETLR() {
      super.SETLR(false);
   }

   /**
   *    INIT - Init subroutine
   */
   public void INIT() {
   }

   // Movex MDB definitions
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBL APIBL;
   public mvx.db.dta.MPSUPR PSUPR;
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.MITMAS ITMAS;
   // Movex MDB definitions end

   public void initMDB() {
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBL = (mvx.db.dta.FAPIBL)getMDB("FAPIBL", APIBL);
      PSUPR = (mvx.db.dta.MPSUPR)getMDB("MPSUPR", PSUPR);
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      ITMAS = (mvx.db.dta.MITMAS)getMDB("MITMAS", ITMAS);
   }

   public cPXAPS450FncINmaintain pAPS450Fnc_maintain = null;
   public cPXAPS450FncINacknowledge pAPS450Fnc_acknowledge = null;
   public cPXAPS450FncINapproveInvoice pAPS450Fnc_approveInvoice = null;
   public cPXAPS450FncINrejectInvoice pAPS450Fnc_rejectInvoice = null;
   public cPXAPS451FncINmaintain pAPS451Fnc_maintain = null;
   public cPXAPS451FncINadjustLine pAPS451Fnc_adjustLine = null;
   public cPXAPS453FncINmaintain pAPS453Fnc_maintain = null;
   public cPXAPS455FncINmaintain pAPS455Fnc_maintain = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINmaintain get_pAPS450Fnc_maintain() {
      if (pAPS450Fnc_maintain == null) {
         cPXAPS450FncINmaintain newPlist = new cPXAPS450FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_maintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_maintain;
      }
   }

   /**
   * Calling APS450Fnc with pAPS450Fnc_maintain as a transaction.
   */
   @Transaction(name=cPXAPS450FncINmaintain.LOGICAL_NAME)
   public void transaction_APS450FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
   }
   
   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINacknowledge get_pAPS450Fnc_acknowledge() {
      if (pAPS450Fnc_acknowledge == null) {
         cPXAPS450FncINacknowledge newPlist = new cPXAPS450FncINacknowledge();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_acknowledge.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_acknowledge;
      }
   }

   /**
   * Calling APS450Fnc with pAPS450Fnc_acknowledge as a transaction.
   */
   @Transaction(name=cPXAPS450FncINacknowledge.LOGICAL_NAME)
   public void transaction_APS450FncINacknowledge() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_acknowledge);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINapproveInvoice get_pAPS450Fnc_approveInvoice() {
      if (pAPS450Fnc_approveInvoice == null) {
         cPXAPS450FncINapproveInvoice newPlist = new cPXAPS450FncINapproveInvoice();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_approveInvoice.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_approveInvoice;
      }
   }

   /**
   * Calling APS450Fnc with pAPS450Fnc_approveInvoice as a transaction.
   */
   @Transaction(name=cPXAPS450FncINapproveInvoice.LOGICAL_NAME)
   public void transaction_APS450FncINapproveInvoice() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_approveInvoice);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINrejectInvoice get_pAPS450Fnc_rejectInvoice() {
      if (pAPS450Fnc_rejectInvoice == null) {
         cPXAPS450FncINrejectInvoice newPlist = new cPXAPS450FncINrejectInvoice();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_rejectInvoice.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_rejectInvoice;
      }
   }

   /**
   * Calling APS450Fnc with pAPS450Fnc_rejectInvoice as a transaction.
   */
   @Transaction(name=cPXAPS450FncINrejectInvoice.LOGICAL_NAME)
   public void transaction_APS450FncINrejectInvoice() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_rejectInvoice);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINmaintain get_pAPS451Fnc_maintain() {
      if (pAPS451Fnc_maintain == null) {
         cPXAPS451FncINmaintain newPlist = new cPXAPS451FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS451Fnc_maintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS451Fnc_maintain;
      }
   }

   /**
   * Calling APS451Fnc with pAPS451Fnc_maintain as a transaction.
   */
   @Transaction(name=cPXAPS451FncINmaintain.LOGICAL_NAME)
   public void transaction_APS451FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAPS451Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS451FncINadjustLine get_pAPS451Fnc_adjustLine() {
      if (pAPS451Fnc_adjustLine == null) {
         cPXAPS451FncINadjustLine newPlist = new cPXAPS451FncINadjustLine();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS451Fnc_adjustLine.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS451Fnc_adjustLine;
      }
   }

   /**
   * Calling APS451Fnc with pAPS451Fnc_adjustLine as a transaction.
   */
   @Transaction(name=cPXAPS451FncINadjustLine.LOGICAL_NAME)
   public void transaction_APS451FncINadjustLine() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS451Fnc", pAPS451Fnc_adjustLine);
      LDAZZ.FPNM.move(XFPGNM);
   }

  /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS453FncINmaintain get_pAPS453Fnc_maintain() {
      if (pAPS453Fnc_maintain == null) {
         cPXAPS453FncINmaintain newPlist = new cPXAPS453FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS453Fnc_maintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS453Fnc_maintain;
      }
   }

   /**
   * Calling APS453Fnc with pAPS453Fnc_maintain as a transaction.
   */
   @Transaction(name=cPXAPS453FncINmaintain.LOGICAL_NAME)
   public void transaction_APS453FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS453Fnc", pAPS453Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS455FncINmaintain get_pAPS455Fnc_maintain() {
      if (pAPS455Fnc_maintain == null) {
         cPXAPS455FncINmaintain newPlist = new cPXAPS455FncINmaintain();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS455Fnc_maintain.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS455Fnc_maintain;
      }
   }

   /**
   * Calling APS455Fnc with pAPS455Fnc_maintain as a transaction.
   */
   @Transaction(name=cPXAPS455FncINmaintain.LOGICAL_NAME)
   public void transaction_APS455FncINmaintain() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pAPS455Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
   }

   public cMICommon MICommon = new cMICommon(this);
   public cCRCommon CRCommon = new cCRCommon(this);
   public cRetrieveUserInfo retrieveUserInfo = new cRetrieveUserInfo(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public sDSCUCD DSCUCD = new sDSCUCD(this);
   public cPLCHKAD PLCHKAD = new cPLCHKAD(this);
   public boolean found_CMNDIV;
   public boolean found_CSYTAB_CUCD;
   public boolean found_MITMAS;
   public MvxString XFPGNM = cRefPGNM.likeDef();
   
   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(APIBH);
      v.addElement(APIBL);
      v.addElement(PSUPR);
      v.addElement(MNDIV);
      v.addElement(SYTAB);
      v.addElement(ITMAS);
      v.addElement(pAPS450Fnc_maintain);
      v.addElement(pAPS450Fnc_acknowledge);
      v.addElement(pAPS450Fnc_approveInvoice);
      v.addElement(pAPS450Fnc_rejectInvoice);
      v.addElement(pAPS451Fnc_maintain);
      v.addElement(pAPS451Fnc_adjustLine);
      v.addElement(pAPS453Fnc_maintain);
      v.addElement(pAPS455Fnc_maintain);
      v.addElement(MICommon);
      v.addElement(CRCommon);
      v.addElement(retrieveUserInfo);
      v.addElement(CRMessageDS);
      v.addElement(DSCUCD);
      v.addElement(PLCHKAD);
      v.addElement(XFPGNM);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      found_CMNDIV = false;
      found_CSYTAB_CUCD = false;
      found_MITMAS = false;
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="_MAK_EONG_140224_04:47";

public final static String _GUID="5AE5B55BA2364b3bA438EA239BA0AE55";

public final static String _tempFixComment="";

public final static String _build="000000000000094";

public final static String _pgmName="APS450MI";

   public String getVersion() {
      return _version;
   }

   public String getRelease() {
      return _release;
   }

   public String getSpLevel() {
      return _spLevel;
   }

   public String getSpNumber() {
      return _spNumber;
   }

   public String getGUID() {
      return _GUID;
   }

   public String getTempFixComment() {
      return _tempFixComment;
   }

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   }

   public String getBuild() {
      return (_version + _release + _build + "      " +  _pgmName + "                                   ").substring(0,34);
   }

   public String [][] getStandardModification() {
      return _standardModifications;
   } // end of method [][] getStandardModification()

   public final static String [][] _standardModifications={
      {"JT-554162","140226","EONG","Due date not transferred with APS450MI"}
   };
}