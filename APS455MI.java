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

import mvx.app.common.*;// Standard imports
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.util.*;


/*
*Modification area - M3
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/

/**
*<BR><B><FONT SIZE=+2>APS455MI Supplier Invoice Batch - submitted</FONT></B><BR><BR>
*
* This class ...<BR><BR>
*
*/
public class APS455MI extends MIBatch
{
   public void movexMain() {
      INIT();
      //   Accept conversation
      MICommon.initiate();
      MICommon.accept();
      while (MICommon.read()) {
         if (MICommon.isTransaction(GET_USER_INFO)) {
            MICommon.setTransaction(retrieveUserInfo.getMessage());
         } else if (MICommon.isTransaction("ValidByBatchNo")) {
            validateByBatchNumber();
         } else if (MICommon.isTransaction("ValidBySelect")) {
            validateBySelection();
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
   * Execute command ValidByBatchNo
   */
   public void validateByBatchNumber() {
      sAPS455MIRValidByBatchNo inValidByBatchNo = (sAPS455MIRValidByBatchNo)MICommon.getInDS(sAPS455MIRValidByBatchNo.class);
      boolean newEntryContext = false;
      if (CRCommon.isCentralUser()) {
         if (inValidByBatchNo.getQ0DIVI().isBlank()) {
            // MSGID=WDI0102 Division must be entered
            MICommon.setError("selectDIVI", "WDI0102");
         }
      } else {
         if (inValidByBatchNo.getQ0DIVI().NE(LDAZD.DIVI)) {
            // MSGID=XDI0008 Division is not permitted
            MICommon.setError("selectDIVI", "XDI0008");
         }
      }
      // Call APS455Fnc, maintain - mode CHANGE, step INITIATE
      // =========================================
      pAPS455Fnc_maintain = get_pAPS455Fnc_maintain();
      pAPS455Fnc_maintain.messages.forgetNotifications();
      pAPS455Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pAPS455Fnc_maintain.indicateAutomated();
      pAPS455Fnc_maintain.manualCall.set(true); // Called manually from APS455MI (as opposed to automatically from a batch job, e.g. PPCRTSBT).
      // Don't update selection record
      pAPS455Fnc_maintain.noUpdate.set(true);
      // Set key parameters
      // - Responsible
      pAPS455Fnc_maintain.RESP.set().moveLeftPad(this.DSUSS);
      // - Report Version
      pAPS455Fnc_maintain.LIVR.set().moveLeftPad(this.DSUSS);
      // - Division
      if (!MICommon.set(pAPS455Fnc_maintain.selectDIVI, inValidByBatchNo.getQ0DIVI())) {
         return;
      }
      // Set selection parameters
      // - Invoice batch number
      if (inValidByBatchNo.getQ0INBN().isBlank()) {
         // MSGID=WINBN02 Invoice batch number must be entered
         MICommon.setError("INBN", "WINBN02");
         return;
      }
      // fromINBN must be set to indicate validate By batch number in initiate
      if (!MICommon.set(pAPS455Fnc_maintain.fromINBN, inValidByBatchNo.getQ0INBN(), "INBN")) {
         return;
      }
      // - Invoice batch operation
      pAPS455Fnc_maintain.IBOP.set(cRefIBOPext.VALIDATE());
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pAPS455Fnc_maintain);
      LDAZZ.FPNM.move(XFPGNM);
      // =========================================
      // Handle messages
      validByBatchNo_handleMessages(pAPS455Fnc_maintain.messages);
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
         validByBatchNo_getInData(inValidByBatchNo);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS455Fnc", pAPS455Fnc_maintain);
         LDAZZ.FPNM.move(this.DSPGM);
         // =========================================
         // Handle messages
         validByBatchNo_handleMessages(pAPS455Fnc_maintain.messages);
         newEntryContext = pAPS455Fnc_maintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS455Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      validByBatchNo_checkIfInvalidFieldsSet(inValidByBatchNo);
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
      validByBatchNo_handleMessages(pAPS455Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS455Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
   }

   /**
   * Get in data for command ValidByBatchNo.
   * @param inValidByBatchNo
   *    DS for in data.
   */
   public void validByBatchNo_getInData(sAPS455MIRValidByBatchNo inValidByBatchNo) {
      // - Division
      if (pAPS455Fnc_maintain.selectDIVI.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.selectDIVI.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.selectDIVI, inValidByBatchNo.getQ0DIVI())) {
         return;
      }
      // Invoice batch number
      if (pAPS455Fnc_maintain.fromINBN.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromINBN.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromINBN, inValidByBatchNo.getQ0INBN(), "INBN")) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command ValidByBatchNo.
   * @param inValidByBatchNo
   *    DS for in data.
   */
   public void validByBatchNo_checkIfInvalidFieldsSet(sAPS455MIRValidByBatchNo inValidByBatchNo) {
      // - Division
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.selectDIVI, inValidByBatchNo.getQ0DIVI(), "DIVI")) {
         return;
      }
      // Invoice batch number
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromINBN, inValidByBatchNo.getQ0INBN(), "INBN")) {
         return;
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void validByBatchNo_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, validByBatchNo_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
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
   public String validByBatchNo_mapFLDI(String FLDI) {
      return FLDI;
   }
   
  /**
   * Execute command ValidBySelect
   */
   public void validateBySelection() {
      sAPS455MIRValidBySelect inValidBySelect = (sAPS455MIRValidBySelect)MICommon.getInDS(sAPS455MIRValidBySelect.class);
      boolean newEntryContext = false;
      if (CRCommon.isCentralUser()) {
         if (inValidBySelect.getQ1DIVI().isBlank()) {
            // MSGID=WDI0102 Division must be entered
            MICommon.setError("selectDIVI", "WDI0102");
         }
      } else {
         if (inValidBySelect.getQ1DIVI().NE(LDAZD.DIVI)) {
            // MSGID=XDI0008 Division is not permitted
            MICommon.setError("selectDIVI", "XDI0008");
         }
      }
      // Call APS455Fnc, maintain - mode CHANGE, step INITIATE
      // =========================================
      pAPS455Fnc_maintain = get_pAPS455Fnc_maintain();
      pAPS455Fnc_maintain.messages.forgetNotifications();
      pAPS455Fnc_maintain.prepare(cEnumMode.CHANGE, cEnumStep.INITIATE);
      pAPS455Fnc_maintain.indicateAutomated();
      pAPS455Fnc_maintain.manualCall.set(true); // Called manually from APS455MI (as opposed to automatically from a batch job, e.g. PPCRTSBT).
      // Don't update selection record
      pAPS455Fnc_maintain.noUpdate.set(true);
      // Set key parameters
      // - Responsible
      pAPS455Fnc_maintain.RESP.set().moveLeftPad(this.DSUSS);
      // - Report Version
      pAPS455Fnc_maintain.LIVR.set().moveLeftPad(this.DSUSS);
      // Set selection parameters
      // - Invoice batch operation
      pAPS455Fnc_maintain.IBOP.set(cRefIBOPext.VALIDATE());
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS455Fnc", pAPS455Fnc_maintain);
      LDAZZ.FPNM.move(this.DSPGM);
      // =========================================
      // Handle messages
      validBySelect_handleMessages(pAPS455Fnc_maintain.messages);
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
         validBySelect_getInData(inValidBySelect);
         // Return error message
         if (MICommon.isError()) {
            return;
         }
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS455Fnc", pAPS455Fnc_maintain);
         LDAZZ.FPNM.move(this.DSPGM);
         // =========================================
         // Handle messages
         validBySelect_handleMessages(pAPS455Fnc_maintain.messages);
         newEntryContext = pAPS455Fnc_maintain.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS455Fnc_maintain.release();
         // Return error message
         if (MICommon.isError()) {
            return;
         }
      } while (newEntryContext);
      validBySelect_checkIfInvalidFieldsSet(inValidBySelect);
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
      validBySelect_handleMessages(pAPS455Fnc_maintain.messages);
      // Release resources allocated by the parameter list.
      pAPS455Fnc_maintain.release();
      // Return error message
      if (MICommon.isError()) {
         return;
      }
   }

   /**
   * Get in data for command ValidBySelect.
   * @param inValidBySelect
   *    DS for in data.
   */
   public void validBySelect_getInData(sAPS455MIRValidBySelect inValidBySelect) {
      // - Division
      if (pAPS455Fnc_maintain.selectDIVI.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.selectDIVI.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.selectDIVI, inValidBySelect.getQ1DIVI())) {
         return;
      }
      // - From supplier invoice number
      if (pAPS455Fnc_maintain.fromSINO.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromSINO.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromSINO, inValidBySelect.getQ1FSIN())) {
         return;
      }
      // - To supplier invoice number
      if (pAPS455Fnc_maintain.toSINO.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toSINO.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toSINO, inValidBySelect.getQ1TSIN())) {
         return;
      }
      // From invoice batch number
      if (pAPS455Fnc_maintain.fromINBN.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromINBN.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromINBN, inValidBySelect.getQ1FINB(), "FINB")) {
         return;
      }
      // To invoice batch number
      if (pAPS455Fnc_maintain.toINBN.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toINBN.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toINBN, inValidBySelect.getQ1TINB(), "TINB")) {
         return;
      }
      // - From Invoice batch type
      if (pAPS455Fnc_maintain.fromIBTP.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromIBTP.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromIBTP, inValidBySelect.getQ1FIBT())) {
         return;
      }
      // - To Invoice batch type
      if (pAPS455Fnc_maintain.toIBTP.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toIBTP.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toIBTP, inValidBySelect.getQ1TIBT())) {
         return;
      }
      // - From invoice status
      if (pAPS455Fnc_maintain.fromSUPA.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromSUPA.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromSUPA, inValidBySelect.getQ1FSUP(), "FSUP")) {
         return;
      }
      // - To invoice status
      if (pAPS455Fnc_maintain.toSUPA.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toSUPA.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toSUPA, inValidBySelect.getQ1TSUP(), "TSUP")) {
         return;
      }
      // - From Supplier
      if (pAPS455Fnc_maintain.fromSUNO.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromSUNO.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromSUNO, inValidBySelect.getQ1FSUN())) {
         return;
      }
      // - To Supplier
      if (pAPS455Fnc_maintain.toSUNO.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toSUNO.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toSUNO, inValidBySelect.getQ1TSUN())) {
         return;
      }
      // - From invoice date
      if (pAPS455Fnc_maintain.fromIVDT.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromIVDT.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.setDate(pAPS455Fnc_maintain.fromIVDT, inValidBySelect.getQ1FIVD(), "FIVD")) {
         return;
      }
      // - To invoice date
      if (pAPS455Fnc_maintain.toIVDT.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toIVDT.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.setDate(pAPS455Fnc_maintain.toIVDT, inValidBySelect.getQ1TIVD(), "TIVD")) {
         return;
      }
      // - From Authorized user
      if (pAPS455Fnc_maintain.fromAPCD.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromAPCD.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromAPCD, inValidBySelect.getQ1FAPC())) {
         return;
      }
      // - To Authorized user
      if (pAPS455Fnc_maintain.toAPCD.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toAPCD.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toAPCD, inValidBySelect.getQ1TAPC())) {
         return;
      }
      // - From invoice batch head error
      if (pAPS455Fnc_maintain.fromIBHE.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromIBHE.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromIBHE, inValidBySelect.getQ1FIBH(), "FIBH")) {
         return;
      }
      // - To invoice batch head error
      if (pAPS455Fnc_maintain.toIBHE.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toIBHE.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toIBHE, inValidBySelect.getQ1TIBH(), "TIBH")) {
         return;
      }
      // - From invoice batch line error
      if (pAPS455Fnc_maintain.fromIBLE.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.fromIBLE.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.fromIBLE, inValidBySelect.getQ1FIBL(), "FIBL")) {
         return;
      }
      // - To invoice batch line error
      if (pAPS455Fnc_maintain.toIBLE.isAccessMANDATORYorOPTIONAL()) {
         pAPS455Fnc_maintain.toIBLE.clearValue(); // Clear CSYSTP value
      }
      if (!MICommon.set(pAPS455Fnc_maintain.toIBLE, inValidBySelect.getQ1TIBL(), "TIBL")) {
         return;
      }
   }

   /**
   * Make sure that fields that are not allowed to set are blank for command ValidBySelect.
   * @param inValidBySelect
   *    DS for in data.
   */
   public void validBySelect_checkIfInvalidFieldsSet(sAPS455MIRValidBySelect inValidBySelect) {
      // - Division
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.selectDIVI, inValidBySelect.getQ1DIVI(), "DIVI")) {
         return;
      }
      // - From supplier invoice number
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromSINO, inValidBySelect.getQ1FSIN(), "FSIN")) {
         return;
      }
      // - To supplier invoice number
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toSINO, inValidBySelect.getQ1TSIN(), "TSIN")) {
         return;
      }
      // From invoice batch number
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromINBN, inValidBySelect.getQ1FINB(), "FINB")) {
         return;
      }
      // To invoice batch number
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toINBN, inValidBySelect.getQ1TINB(), "TINB")) {
         return;
      }
      // - From Invoice batch type
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromIBTP, inValidBySelect.getQ1FIBT(), "FIBT")) {
         return;
      }
      // - To Invoice batch type
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toIBTP, inValidBySelect.getQ1TIBT(), "TIBT")) {
         return;
      }
      // - From invoice status
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromSUPA, inValidBySelect.getQ1FSUP(), "FSUP")) {
         return;
      }
      // - To invoice status
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toSUPA, inValidBySelect.getQ1TSUP(), "TSUP")) {
         return;
      }
      // - From Supplier
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromSUNO, inValidBySelect.getQ1FSUN(), "FSUN")) {
         return;
      }
      // - To Supplier
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toSUNO, inValidBySelect.getQ1TSUN(), "TSUN")) {
         return;
      }
      // - From invoice date
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromIVDT, inValidBySelect.getQ1FIVD(), "FIVD")) {
         return;
      }
      // - To invoice date
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toIVDT, inValidBySelect.getQ1TIVD(), "TIVD")) {
         return;
      }
      // - From Authorized user
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromAPCD, inValidBySelect.getQ1FAPC(), "FAPC")) {
         return;
      }
      // - To Authorized user
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toAPCD, inValidBySelect.getQ1TAPC(), "TAPC")) {
         return;
      }
      // - From invoice batch head error
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromIBHE, inValidBySelect.getQ1FIBH(), "FIBH")) {
         return;
      }
      // - To invoice batch head error
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toIBHE, inValidBySelect.getQ1TIBH(), "TIBH")) {
         return;
      }
      // - From invoice batch line error
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.fromIBLE, inValidBySelect.getQ1FIBL(), "FIBL")) {
         return;
      }
      // - To invoice batch line error
      if (MICommon.checkIfNotAllowed(pAPS455Fnc_maintain.toIBLE, inValidBySelect.getQ1TIBL(), "TIBL")) {
         return;
      }
   }

   /**
   * Handles messages returned from the function program.
   * @param messages
   *    A reference to the messages.
   */
   public void validBySelect_handleMessages(cCRMessageList messages) {
      messages.prepareGetNext();
      while (messages.getNext(CRMessageDS)) {
         if (MICommon.setError(CRMessageDS, validBySelect_mapFLDI(CRMessageDS.getPXFLDI().toStringRTrim()), messages)) {
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
   public String validBySelect_mapFLDI(String FLDI) {
      return FLDI;
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
   public mvx.db.dta.CSYCAL SYCAL;
   public mvx.db.dta.CMNCMP MNCMP;
   // Movex MDB definitions end

   public void initMDB() {
      SYCAL = (mvx.db.dta.CSYCAL)getMDB("CSYCAL", SYCAL);
      SYCAL.setAccessProfile("00", 'R');
      MNCMP = (mvx.db.dta.CMNCMP)getMDB("CMNCMP", MNCMP);
      MNCMP.setAccessProfile("00", 'R');
   }

   public cPXAPS455FncINmaintain pAPS455Fnc_maintain = null;
   
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
      LDAZZ.FPNM.move(this.DSPGM);
   }
   
   public cRetrieveUserInfo retrieveUserInfo = new cRetrieveUserInfo(this);
   public cMICommon MICommon = new cMICommon(this);
   public cCRCommon CRCommon = new cCRCommon(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);
   public boolean found_CMNCMP;
   public MvxString XFPGNM = cRefPGNM.likeDef();

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(SYCAL);
      v.addElement(MNCMP);
      v.addElement(pAPS455Fnc_maintain);
      v.addElement(retrieveUserInfo);
      v.addElement(MICommon);
      v.addElement(CRCommon);
      v.addElement(CRMessageDS);
      v.addElement(XFPGNM);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      found_CMNCMP = false;
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="6FF8BE697F4F4fbf93FE9B7A441D4B8E";

public final static String _tempFixComment="";

public final static String _build="000000000000028";

public final static String _pgmName="APS455MI";

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
   }

  public final static String [][] _standardModifications={};
}


