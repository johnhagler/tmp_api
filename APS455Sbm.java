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
import mvx.db.common.Expression;
import mvx.db.common.FieldSelection;

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
*<BR><B><FONT SIZE=+2>Lst: Supplier Invoice Batch - submitted job</FONT></B><BR><BR>
*
* This program is submitted as a batch job from APS455/APS455Fnc,
* and performs operations on a selection of records in FAPIBH.
*/
public class APS455Sbm extends Batch 
{
   public void movexMain() {
      INIT();
      if (flgEXIT) {
         return;
      }
      // Perform operation on selected records.
      if (IBOP == cRefIBOPext.DELETE()) {
         do_delete();
      } else if (IBOP == cRefIBOPext.PRINT()) {
         do_print();
      } else if (IBOP == cRefIBOPext.VALIDATE()) {
         do_validate();
      } else if (IBOP == cRefIBOPext.UPDATE_TO_APL()) {
         do_updateToAPL();
      } else {
         operationError();
         return;
      }
      SETLR();
      return;
   }

   /**
   * Delete selected invoices.
   */
   public void do_delete() {
      while (APIBH.READ("RR")) {
         this.PXCONO = APIBH.getCONO();
         this.PXDIVI.clear(); 	
         deleteInvoice();
      }
   }

   /**
   * Deletes the current invoice in APIBH.
   */
   public void deleteInvoice() {
      boolean error = false;
      boolean newEntryContext = false;
      // Call APS450Fnc, delete - step INITIATE
      // =========================================
      pAPS450Fnc_delete = get_pAPS450Fnc_delete();
      pAPS450Fnc_delete.messages.forgetNotifications();
      pAPS450Fnc_delete.prepare(cEnumStep.INITIATE);
      pAPS450Fnc_delete.indicateAutomated();
      // Set key parameters
      // - Division
      pAPS450Fnc_delete.DIVI.set().moveLeftPad(APIBH.getDIVI());
      // - Invoice batch number
      pAPS450Fnc_delete.INBN.set(APIBH.getINBN());
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_delete);
      LDAZZ.FPNM.move(this.DSPGM);
      // =========================================
      // Handle messages
      error = pAPS450Fnc_delete.messages.existError();
      // Release resources allocated by the parameter list.
      pAPS450Fnc_delete.release();
      // Abort if error
      if (error) {
         return;
      }
      // Call APS450Fnc, delete - step VALIDATE
      // =========================================
      do {
         pAPS450Fnc_delete.prepare(cEnumStep.VALIDATE);
         // Move fields to function parameters
         // - No fields to move
         // =========================================
         XFPGNM.move(LDAZZ.FPNM);
         LDAZZ.FPNM.move(this.DSPGM);
         apCall("APS450Fnc", pAPS450Fnc_delete);
         LDAZZ.FPNM.move(this.DSPGM);
         // =========================================
         // Handle messages
         error = pAPS450Fnc_delete.messages.existError();
         newEntryContext = pAPS450Fnc_delete.isNewEntryContext();
         // Release resources allocated by the parameter list.
         pAPS450Fnc_delete.release();
         // Abort if error
         if (error) {
            return;
         }
      } while (newEntryContext);
      // Call APS450Fnc, delete - step UPDATE
      // =========================================
      pAPS450Fnc_delete.prepare(cEnumStep.UPDATE);
      // =========================================
      int transStatus = executeTransaction(pAPS450Fnc_delete.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS450Fnc_delete.messages, transStatus);
      // =========================================
      // Handle messages
      error = pAPS450Fnc_delete.messages.existError();
      // Release resources allocated by the parameter list.
      pAPS450Fnc_delete.release();
      // Abort if error
      if (error) {
         return;
      }
   }

   /**
   * Print selected invoices.
   */
   public void do_print() {
      if (APIBH.READ("RR")) {
         this.PXCONO = APIBH.getCONO();
         this.PXDIVI.clear(); 	
         printInvoice();
      }
   }

   /**
   * Prints the current invoice in APIBH.
   */
   public void printInvoice() {
      // Set Invoice batch number to status - Printing in progress
      if (!lockInvoiceForPrint(APIBH.getDIVI(), APIBH.getINBN())) {
         // Delete output definitions (CCTLSF, CSFOUT) created for this job
         CROutput.clearOutputDefinitions(PXBJNO, MNS213DS, rAPIDS, APIDS, PXOPC, pMNS213);
         return;
      }
      // Call output programs
      rAPS456.reset();
      rAPS456.set(PXBJNO);
      apCall("APS456", rAPS456);
      // Call APS457
      if (APS455DS.getZWLITP() == 2) {
         rAPS456.reset();
         rAPS456.set(PXBJNO);
        apCall("APS457", rAPS456);
      }
      // Set new status on invoice - SUPA
      if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) 
      {
         if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
            if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
                APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) 
            {
               if (APIBH.getSUPA() == cRefSUPAext.VALIDATED()) {
                  APIBH.setSUPA(cRefSUPAext.PRINTED());
               } else if (APIBH.getSUPA() == cRefSUPAext.ADJUSTED_NOT_REPRINTED()) {
                  APIBH.setSUPA(cRefSUPAext.ADJUSTED_AND_REPRINTED());
               }
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) 
            {
               if (APIBH.getSUPA() == cRefSUPAext.VALIDATED()) {
                  APIBH.setSUPA(cRefSUPAext.PRINTED());
               }
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING())) {
               int SUAClimitDate = 0;
               // Next status depends on supplier acceptance settings - SUAC
               if (APIBH.getSUAC() == 3) {
                  // Check self billing agreement
                  int AUT3 = 0;
                  APIBL.setCONO(APIBH.getCONO());
                  APIBL.setDIVI().move(APIBH.getDIVI());
                  APIBL.setINBN(APIBH.getINBN());
                  APIBL.setRDTP(cRefRDTPext.ITEM_LINE());
                  APIBL.SETLL("10", APIBL.getKey("10", 4));
                  if (APIBL.READE("10", APIBL.getKey("10", 4))) {
                     found_MPAGRS = cRefSBANext.getMPAGRS(PAGRS, found_MPAGRS, APIBH.getCONO(), APIBH.getSUNO(), APIBL.getSBAN());
                     AUT3 = PAGRS.getAUT3();
                  }
                  // Calculate SUAC limit date
                  if (CRCalendar.lookUpDate(APIBH.getCONO(), APIBH.getDIVI(), APIBH.getIVDT())) {
                     if (CRCalendar.convertCenturyDayNo(APIBH.getCONO(), APIBH.getDIVI(), 
                            CRCalendar.getCenturyDayNo() + AUT3)) 
                     {
                        SUAClimitDate = CRCalendar.getDate();
                     }
                  }
               }
               if (APIBH.getSUAC() == 0 ||
                   APIBH.getSUAC() == 1 ||
                   APIBH.getSUAC() == 3 && movexDate() >= SUAClimitDate)
               {
                  APIBH.setSUPA(cRefSUPAext.APPROVED());
               } else {
                  // SUAC 2 or SUAC 3 but within time limit --> held for approval
                  APIBH.setSUPA(cRefSUPAext.PRINTED());
               }
            }
            APIBH.UPDAT("00");
         }
      }
      // Set Invoice batch number to status - Unlocked
      unlockInvoice(APIBH.getDIVI(), APIBH.getINBN());
   }

   /**
   * Validate selected invoices.
   * Also might print depending on automation settings.
   * Also might update to accounts payable ledger depending on automation settings.
   */
   public void do_validate() {
      while (APIBH.READ("RR")) {
         this.PXCONO = APIBH.getCONO();
         this.PXDIVI.clear(); 	
         validateInvoice();
      }
   }

   /**
   * Validates the current invoice in APIBH.
   * Also might print the invoice depending on automation settings.
   * Also might update to accounts payable ledger depending on automation settings.
   */
   public void validateInvoice() {
      boolean error = false;
      // Call APS450Fnc, validateInv
      // =========================================
      pAPS450Fnc_validateInv = get_pAPS450Fnc_validateInv();
      pAPS450Fnc_validateInv.messages.forgetNotifications();
      pAPS450Fnc_validateInv.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_validateInv.DIVI.set().moveLeftPad(APIBH.getDIVI());
      // - Invoice batch number
      pAPS450Fnc_validateInv.INBN.set(APIBH.getINBN());
      // =========================================
      int transStatus = executeTransaction(pAPS450Fnc_validateInv.getTransactionName(), /*returnOnFailure*/ true);
      CRCommon.setDBTransactionErrorMessage(pAPS450Fnc_validateInv.messages, transStatus);
      // =========================================
      // Handle messages
      error = pAPS450Fnc_validateInv.messages.existError();
      // Release resources allocated by the parameter list.
      pAPS450Fnc_validateInv.release();
      // Abort if error
      if (error) {
         return;
      }
      // print and update APL if auto update should be performed
      found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, APIBH.getCONO(), pAPS450Fnc_validateInv.DIVI.set(), pAPS450Fnc_validateInv.INBN.get());
      if (found_FAPIBH && APS450Fnc.statusOKForPrint(APIBH)) {
         if (isAutomaticPrintoutOfInvoice()) {
            // Invoice
            CROutput.selectOutputDefs_inBatch(APIBH.getCONO(), LDAZD.DIVI, PXBJNO,
               "APS456PF", PXMNS210);
            // Detailed printout
            if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
                APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
               APS455DS.setZWLITP(2);
               CROutput.selectOutputDefs_inBatch(APIBH.getCONO(), LDAZD.DIVI, PXBJNO,
                  "APS457PF", PXMNS210);
            }
            printInvoice();
         }
         found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, APIBH.getCONO(), pAPS450Fnc_validateInv.DIVI.set(), pAPS450Fnc_validateInv.INBN.get());
         if (found_FAPIBH && APS450Fnc.statusOKForUpdAPL(APIBH)) {
            if (isAutomaticUpdateOfAPL()) {
               CROutput.selectOutputDefs_inBatch(APIBH.getCONO(), LDAZD.DIVI, PXBJNO,
               "GLS041PF", PXMNS210);
               updateInvoiceToAPL();
               // Delete dummy output definitions (CCTLSF, CSFOUT) for GLS041PF on job number PXBJNO (They were copied to a new job number for the actual printout).
               CROutput.clearOutputDefinitions(PXBJNO, "GLS041PF", MNS213DS, rAPIDS, APIDS, PXOPC, pMNS213);
            }
         }
      }
      if (!manualCall &&
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || 
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
         found_FAPIBH = cRefINBNext.getFAPIBH(APIBH, false, APIBH.getCONO(), pAPS450Fnc_validateInv.DIVI.set(), pAPS450Fnc_validateInv.INBN.get());
         if (found_FAPIBH) {
            generateMailBoxMessage();
         }
      }
      return;
   }

   /**
   * Returns true if automatic printout of invoice should be done
   */
   public boolean isAutomaticPrintoutOfInvoice() {
      if (manualCall) {
         // APS455Sbm is called manually from APS455
         setFamFunction();
         getFamFunction();
         if (DSFFNC.getDFAUTU() == 1 ||
             DSFFNC.getDFAUTU() == 2 &&
            APIBH.getIBHE() != cRefIBHEext.WARNINGS() &&
             APIBH.getIBLE() != cRefIBLEext.WARNINGS()) 
         {
            return true;
         }
      } else {
         if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || 
             APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) 
         {
            GINDH.setCONO(APIBH.getCONO());
            GINDH.setDIVI().move(APIBH.getDIVI());
            GINDH.setSUNO().move(APIBH.getSUNO());
            GINDH.setDNNR().move(APIBH.getSINO());
            if (GINDH.CHAIN("00", GINDH.getKey("00", 4))) {
               //   Read AP Standard document
               found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, GINDH.getCONO(), GINDH.getDIVI(), GINDH.getSDAP());
               cRefSDAPext.setDSSDAP(SYTAB, DSSDAP);
               if (DSSDAP.getYUSDPR() == 1 &&
                  APIBH.getIBHE() == cRefIBHEext.NO_ERRORS() &&
                  APIBH.getIBLE() == cRefIBLEext.NO_ERRORS())
               {
                  return true;
               }
            }
         }
         if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) &&
             APIBH.getUPBI() == 1 && // One invoice per receiving number
             APIBH.getIBHE() == cRefIBHEext.NO_ERRORS() &&
             APIBH.getIBLE() == cRefIBLEext.NO_ERRORS()) 
         {
            // Check self billing agreement
            int AUT1 = 0;
            int AUT2 = 0;
            APIBL.setCONO(APIBH.getCONO());
            APIBL.setDIVI().move(APIBH.getDIVI());
            APIBL.setINBN(APIBH.getINBN());
            APIBL.setRDTP(cRefRDTPext.ITEM_LINE());
            APIBL.SETLL("10", APIBL.getKey("10", 4));
            if (APIBL.READE("10", APIBL.getKey("10", 4))) {
               found_MPAGRS = cRefSBANext.getMPAGRS(PAGRS, found_MPAGRS, APIBH.getCONO(), APIBH.getSUNO(), APIBL.getSBAN());
               AUT1 = PAGRS.getAUT1();
               AUT2 = PAGRS.getAUT2();
            }
            if (AUT2 == 3 && AUT1 == 2) {
               return true;
            }
         }
         if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) || 
             APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) 
         {
            // Get SCIS - Rebate claim invoide setting ID
            foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
            foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
            cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, APIBH.getCONO(), APIBH.getDIVI(),  APIBH.getSUNO());
            found_CIDVEN = foundParam_CIDVEN.getBoolean();
            found_CSUDIV = foundParam_CSUDIV.getBoolean();
            // Retrieve supplier claim invoice settings
            found_MPSUPS = cRefSCISext.getMPSUPS(PSUPS, found_MPSUPS, APIBH.getCONO(), IDVEN.getSCIS());
            if (found_MPSUPS && PSUPS.getAUT4() == cRefAUT4ext.VALIDATE_AND_PRINT()) {
               return true;
            }
         }
      }
      return false;
   }

  /**
   * Returns true if automatic update of APL should be done
   */
   public boolean isAutomaticUpdateOfAPL() {
      int testACDT = 0;
      if (manualCall) {
         // APS455Sbm is called manually from APS455
         setFamFunction();
         getFamFunction();
         // Check automation settings
         if (DSFFNC.getDFAUTU() == 1 ||
             DSFFNC.getDFAUTU() == 2 &&
             APIBH.getIBHE() != cRefIBHEext.WARNINGS() &&
             APIBH.getIBLE() != cRefIBLEext.WARNINGS()) 
         {
            // Check valid accounting date
            if (movexDate() >= DSFFNC.getDFFRDT() && movexDate() <= DSFFNC.getDFTODT()) {
               return true;
            }
         }
      } else {
         if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE())) 
         {
            // Check if accouting date within date limits of FAM function
            setFamFunction();
            getFamFunction();
            testACDT = APIBH.getACDT();
            if (testACDT == 0) {
               testACDT = movexDate();
            }
            if (testACDT >= DSFFNC.getDFFRDT() && testACDT <= DSFFNC.getDFTODT()) {
               GINDH.setCONO(APIBH.getCONO());
               GINDH.setDIVI().move(APIBH.getDIVI());
               GINDH.setSUNO().move(APIBH.getSUNO());
               GINDH.setDNNR().move(APIBH.getSINO());
               if (GINDH.CHAIN("00", GINDH.getKey("00", 4))) {
                  // Read AP Standard document
                  found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, GINDH.getCONO(), GINDH.getDIVI(), GINDH.getSDAP());
                  cRefSDAPext.setDSSDAP(SYTAB, DSSDAP);
                  if (DSSDAP.getYUSDAU() == 1 &&
                     APIBH.getIBHE() == cRefIBHEext.NO_ERRORS() &&
                     APIBH.getIBLE() == cRefIBLEext.NO_ERRORS())
                  {
                     return true;
                  }
               }
            }
         }
         if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) &&
             APIBH.getUPBI() == 1 && // One invoice per receiving number
             APIBH.getIBHE() == cRefIBHEext.NO_ERRORS() &&
             APIBH.getIBLE() == cRefIBLEext.NO_ERRORS()) 
         {
            // Check self billing agreement
            int AUT2 = 0;
            APIBL.setCONO(APIBH.getCONO());
            APIBL.setDIVI().move(APIBH.getDIVI());
            APIBL.setINBN(APIBH.getINBN());
            APIBL.setRDTP(cRefRDTPext.ITEM_LINE());
            APIBL.SETLL("10", APIBL.getKey("10", 4));
            if (APIBL.READE("10", APIBL.getKey("10", 4))) {
               found_MPAGRS = cRefSBANext.getMPAGRS(PAGRS, found_MPAGRS, APIBH.getCONO(), APIBH.getSUNO(), APIBL.getSBAN());
               AUT2 = PAGRS.getAUT2();
            }
            if (AUT2 == 3) {
               return true;
            }
         }
      }
      return false;
   }

  /**
   * Generate Mail Box Message
   */
   public void generateMailBoxMessage() {
      GINDH.setCONO(APIBH.getCONO());
      GINDH.setDIVI().move(APIBH.getDIVI());
      GINDH.setSUNO().move(APIBH.getSUNO());
      GINDH.setDNNR().move(APIBH.getSINO());
      if (GINDH.CHAIN("00", GINDH.getKey("00", 4))) {
         //   Read AP Standard document
         found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, GINDH.getCONO(), GINDH.getDIVI(), GINDH.getSDAP());
         cRefSDAPext.setDSSDAP(SYTAB, DSSDAP);
      } else {
         DSSDAP.setDSSDAP().clear();
      }
      //   Clear parameter list
      CRS428DS.setCRS428DS().clear();
      CRS428DS.setC1CONO(APIBH.getCONO());
      CRS428DS.setC1REC2().move(APIBH.getAPCD());
      CRS428DS.setC1ADAT(movexDate());
      CRS428DS.setC1PAR1().moveLeft(APIBH.getSINO());
      CRS428DS.setC1PAR2().moveLeft(APIBH.getSUNO());
      CRS428DS.setC1PAR3().moveLeft(this.DSUSS);
      switch (DSSDAP.getYUSDTP()) {
         case 1:
            switch (0) {
               default:
                  if (DSSDAP.getYUSDPR() == 1 &&
                      DSSDAP.getYUSDAU() == 0 &&
                      APIBH.getSUPA() >= cRefSUPAext.PRINTED()) {
                     //   Debit note
                     //   "Debit note - Created and printed"
                     CRS428DS.setC1MTPE().move("161");
                     break;
                  }
                  if (DSSDAP.getYUSDPR() == 1 &&
                      DSSDAP.getYUSDAU() == 1 &&
                      APIBH.getSUPA() >= cRefSUPAext.UPDATED_IN_APL()) {
                     //   "Debit note - Created, printed and updated in AP"
                     CRS428DS.setC1MTPE().move("162");
                     break;
                  }
                  //   "Debit note - Only created"
                  CRS428DS.setC1MTPE().move("160");
                  break;
            }
            break;
         case 2:
            switch (0) {
               default:
                  if (DSSDAP.getYUSDPR() == 1 &&
                      DSSDAP.getYUSDAU() == 0 &&
                      APIBH.getSUPA() >= cRefSUPAext.PRINTED()) {
                     //   Debit note request
                     //   "Debit note request - Created and printed"
                     CRS428DS.setC1MTPE().move("164");
                     break;
                  }
                  //   "Debit note request- Only created"
                  CRS428DS.setC1MTPE().move("163");
                  break;
            }
            break;
      }
      CRS428PDS.setCRS428PDS().clear();
      CRS428PDS.setC2CONO(APIBH.getCONO());
      CRS428PDS.setC2PGNM().moveLeft("APS450");
      CRS428PDS.setC2PICC().moveLeft("BI");
      CRS428PDS.setC2FILE().moveLeftPad("FAPIBH");
      String keyFields = getPrimaryKeyForTable("FAPIBH");
      moveToArray(KSTR,0,keyFields);
      CRS428PDS.setC2KSTR().moveLeftPad(KSTR);
      rPL428preCall();
      apCall("CRS428", rPL428);
      rPL428postCall();
   }

   /**
   * Update selected invoices to accounts payable.
   */
   public void do_updateToAPL() {
      currentBCHN = 0;
      while (APIBH.READ("RR")) {
         this.PXCONO = APIBH.getCONO();
         this.PXDIVI.clear(); 	
         updateInvoiceToAPL();
      }
      // Delete dummy output definitions (CCTLSF, CSFOUT) for GLS041PF on job number PXBJNO. (They were copied to a new job number for the actual printout).
      CROutput.clearOutputDefinitions(PXBJNO, "GLS041PF", MNS213DS, rAPIDS, APIDS, PXOPC, pMNS213);
   }

   /**
   * Updates to APL the current invoice in APIBH.
   */
   public void updateInvoiceToAPL() {
      if (!APS450Fnc.statusOKForUpdAPL(APIBH)) {
          return;
      }
      // Check that all lines putaway - if selfbilling, manual call in APS455, and AUT2=2
      if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING()) && manualCall) {
         boolean allRequiredLinesPutAway = true;
         APIBL.setCONO(APIBH.getCONO());
         APIBL.setDIVI().move(APIBH.getDIVI());
         APIBL.setINBN(APIBH.getINBN());
         APIBL.setRDTP(cRefRDTPext.ITEM_LINE());
         APIBL.SETLL("10", APIBL.getKey("10", 4));
         while (APIBL.READE("10", APIBL.getKey("10", 4))) {
            found_MPAGRS = cRefSBANext.getMPAGRS(PAGRS, found_MPAGRS, APIBH.getCONO(), APIBH.getSUNO(), APIBL.getSBAN());
            // Check if put away
            if (PAGRS.getAUT2() == 2) {
               PPRTVQTYDS.clear();
               PPRTVQTYDS.setP6CONO(APIBL.getCONO());
               PPRTVQTYDS.setP6REPN(APIBL.getREPN());
               PPRTVQTYDS.setP6PGNM().moveLeft("PPS350");
               PPRTVQTYDS.setP6CHKF(3);
               PPRTVQTYDS.setP6PUNO().move(APIBL.getPUNO());
               PPRTVQTYDS.setP6PNLI(APIBL.getPNLI());
               PPRTVQTYDS.setP6PNLS(APIBL.getPNLS());
               pPPRTVQTYpreCall();
               apCall("PPRTVQTY", pPPRTVQTY);
               pPPRTVQTYpostCall();
               if (!isBlank(PPRTVQTYDS.getP6RPQA(), cRefORQA.decimals())) {
                  allRequiredLinesPutAway = false;
                  break;
               }
            }
         }
         if (!allRequiredLinesPutAway) {
            return;
         }
      }
      getCMNDIV(APIBH.getDIVI());
      // Accounting date must be set
      if (APIBH.getACDT() != 0) {
         currentACDT = APIBH.getACDT();
      } else {
         if (APS455DS.getZWACDT() != 0) {
            currentACDT = APS455DS.getZWACDT();
         } else {
            currentACDT = movexDate();
         }
      }
      this.PXDIVI.move(APIBH.getDIVI()); 	 	
      this.PXDFMI.move("YMD8"); 	 	
      this.PXDATI = currentACDT; 	 	
      this.PXDFMO.move("YMD8"); 	 	
      this.PXOPRM = 0; 	 	
      this.PXDSEP = ' '; 	 	
      COMDAT(); 	 	
      switch (XXPTFA) { 	 	
         case 1: 	 	
            currentACYP = this.PXCYP1; 	 	
            break; 	 	
         case 2: 	 	
            currentACYP = this.PXCYP2; 	 	
            break; 	 	
         case 3: 	 	
            currentACYP = this.PXCYP3; 	 	
            break; 	 	
         case 4: 	 	
            currentACYP = this.PXCYP4; 	 	
            break; 	 	
         case 5: 	 	
            currentACYP = this.PXCYP5; 	 	
            break; 	 	
         default:
            currentACYP = 0;
            break; 	 	
      }
      //   Init fields for CCLCCUR
      PLCLCCU.FZCMTP = LDAZD.CMTP;
      PLCLCCU.FZCONO = APIBH.getCONO();
      PLCLCCU.FZDIVI.move(APIBH.getDIVI());
      PLCLCCU.FZDMCU = XXDMCU;
      PLCLCCU.FZCRTP = 0;
      PLCLCCU.FZCUTD = 0;
      PLCLCCU.FZRAFA = 0;

      getCRS750DS(APIBH.getDIVI());
      if (!SYPAR.getDIVI().isBlank() &&
          CRS750DS.getPBIVCC() == 1) {
         getInvoiceClass = true;
      } else {
         getInvoiceClass = false;
      }
      found_CIDMAS = cRefSUNOext.getCIDMAS(IDMAS, found_CIDMAS, APIBH.getCONO(), APIBH.getSUNO());
      // Check if VAT accounting shall be done
      CheckIfVat();
      setFamFunction();
      getFamFunction();
      // Set Invoice batch number to status - Update in progress
      if (!lockInvoiceForUpdAPL(APIBH.getDIVI(), APIBH.getINBN())) {
         // Invoice could not be locked for update
         return;
      }
      retrieveVoucherNumber();
      initFieldsInFCR040();
      //   Create Accounts payable
      INVADD();
      if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         claimAdjustment();
      }
      //   Create Cash discount transaction
      CDIADD();
      //   Create VAT transactions
      if (XXCVAT == 0) {
         VATADD();
      }
      //   Create Adjustment transactions
      ADJADD();
      // Prepayment
      if (isPrePaymentActivated() && 
         !APIBH.getPPYR().isBlank() && 
         APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
         createReconciledPayment();
      } else {
         //   Invoice matching
         if (APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING() ||
             APIBH.getIMCD() == cRefIMCDext.PO_HEAD_MATCHING() &&
             !APIBH.getPUNO().isBlank()) {
            INVMA();
         } else {
            if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
                APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
               claimAgainstSupplier();
            } else {
               //   Create Charge transactions
               CHGADD();
            }
            //   Clearing account
            if (!isBlank(XTCUAM, cRefCUAM.decimals())) { 	
               X1CUAM = -(XTCUAM);
               X1ACAM = 0d;
               //   Auto VAT 4 = Clearing account splitted per VAT code 	 	 	
               if (automaticVATaccounting_AVAT != 4) { 	 	 	 	
                  CLEADD();
                  if (XXCVAT != 0 && !computeVAT) { 	 	
                     writeVATRecord();     	
                  }
               } else { 	 	
                  splitClearAccByVTCD(); 	 	 	
               } 	 	 	
            }
         }
         //   Create AP50-280 if unbalanced voucher
         if (isBlank(XTCUAM, cRefCUAM.decimals()) && !isBlank(XTACAM, cRefACAM.decimals())) {
            CR280();
         }
      }
      // Set status updated in APL
      if (APIBH.CHAIN_LOCK("00", APIBH.getKey("00"))) {
         APIBH.setSUPA(cRefSUPAext.UPDATED_IN_APL());
         APIBH.setVONO(currentVONO);
         if (CRS750DS.getPBACBC() == 1) {
            APIBH.setVSER().moveLeftPad(currentVSER);
         } else {
            APIBH.setVSER().clear();
         }
         APIBH.setACDT(currentACDT);
         APIBH.UPDAT("00");
      }
      // Set Invoice batch number to status - Unlocked
      unlockInvoice(APIBH.getDIVI(), APIBH.getINBN());
      updateGeneralLedger();   
   }

  /**
   * Returns true if pre-payment is activated in CRS750
   * @return true if pre-payment is activated in CRS750
   */
   public boolean isPrePaymentActivated() {
      if (CRS750DS.getPBPRPY() == 1) {   	
         return true; 
      } else {
         return false; 	
      }
   }
   
   /**
   * CheckIfVat - Check if VAT accountings shall be done
   */
   public void CheckIfVat() {
      XXCVAT = 0;
      XXVTCD = 0;
      if (!APIBH.getFTCO().isBlank()) {
         XXFTCO.moveLeft(APIBH.getFTCO());
      } else {
         XXFTCO.moveLeft(IDMAS.getCSCD());
      }
      if (!APIBH.getBSCD().isBlank()) {
         XXBSCD.moveLeft(APIBH.getBSCD());
      } else {
         XXBSCD.moveLeft(MNDIV.getCSCD());
      }
      XXECAR.moveLeft(IDMAS.getECAR());
      if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
         PLCHKIF.FTCONO = APIBH.getCONO();
         PLCHKIF.FTDIVI.move(APIBH.getDIVI());
         PLCHKIF.FTTASK = 1;
         if (XXECAR.NE(IDMAS.getCSCD()) || XXBSCD.NE(MNDIV.getCSCD())) {
            PLCHKIF.FTTYPE = 6;
            PLCHKIF.FTCSCD.move(XXFTCO);
            PLCHKIF.FTBSCD.move(XXBSCD);
            PLCHKIF.FTECAR.move(XXECAR);
         } else {
            PLCHKIF.FTTYPE = 2;
            PLCHKIF.FTCSCD.clear();
            PLCHKIF.FTBSCD.clear();
            PLCHKIF.FTECAR.clear();
         }
         PLCHKIF.FTPGNM.clear();
         PLCHKIF.FTPGNM.moveLeft("*INTER");
         PLCHKIF.FTWHLO.clear();
         PLCHKIF.FTITNO.clear();
         PLCHKIF.FTCUNO.clear();
         PLCHKIF.FTSUNO.move(APIBH.getSUNO());
         PLCHKIF.FTTXAP = 9;
         IN92 = PLCHKIF.CCHKIFV();
         if (PLCHKIF.FTCLCV == 1) {
            XXVTCD = PLCHKIF.FTVTCD;
         }
         XXEUVT = PLCHKIF.FTEUVT;
         // If EU-VAT and invoice matching, VAT is created in APMNGI03
         if (PLCHKIF.FTEUVT == 1 &&
             APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING() ||
             PLCHKIF.FTEUVT == 1 &&
             APIBH.getIMCD() == cRefIMCDext.PO_HEAD_MATCHING()) {
            if (APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE()) && APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
               // Not debit note
               XXCVAT = 1;
            }
         }
      }
      VATT08 = 0;
      if (XXCVAT == 0 && XXEUVT == 0 && PLCHKIF.FTCLCV == 1) {
         found_CSYTAB_VTCD = cRefVTCDext.getCSYTAB_VTCD(SYTAB, found_CSYTAB_VTCD, LDAZD.CMTP, APIBH.getCONO(), PLCHKIF.FTDIVI, PLCHKIF.FTVTCD);
         if (found_CSYTAB_VTCD) {
            cRefVTCDext.setDSVTCD(SYTAB, DSVTCD);
            if (DSVTCD.getYKVATT() == 8) {
               // Reverse charge of VAT.
               VATT08 = 1;
            }
         }
      }
   }

   public void setFamFunction() {
      switch (0) {
         default:
            if (APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
               //   Pre payment Pre invoice
               XXFEID.move("AP53");
               XXFNCN = 1;
               break;
            }
            if (APIBH.getIBTP().EQ("06")) {
               //   Pre payment Final invoice
               XXFEID.move("AP54");
               XXFNCN = 1;
               break;
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.SELF_BILLING())) {
               //   From MPFAMF (PPS114) if self billing
               PFAMF.setCONO(APIBH.getCONO());
               PFAMF.setFEID().clear();
               PFAMF.setDIVI().moveLeftPad(APIBH.getDIVI());
               PFAMF.setSUNO().clear();
               PFAMF.setFEID().move("AP52");
               PFAMF.setSUNO().move(APIBH.getSUNO());
               if (PFAMF.CHAIN("00", PFAMF.getKey("00", 4))) {
                  XXFNCN = PFAMF.getFNCN();
                  XXFEID.move("AP52");
               } else {
                  PFAMF.setSUNO().clear();
                  if (PFAMF.CHAIN("00", PFAMF.getKey("00", 4))) {
                     XXFNCN = PFAMF.getFNCN();
                     XXFEID.move("AP52");
                  } else {
                     XXFEID.move("AP50");
                     XXFNCN = 1;
                  }
               }
               break;
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
               //   Debit note
               XXFEID.move("AP51");
               //   Read AP Standard document
               found_CSYTAB_SDAP = cRefSDAPext.getCSYTAB_SDAP(SYTAB, found_CSYTAB_SDAP, APIBH.getCONO(), APIBH.getDIVI(), APIBH.getSDAP());
               cRefSDAPext.setDSSDAP(SYTAB, DSSDAP);
               if (found_CSYTAB_SDAP &&
                   DSSDAP.getYUFNCN() != 0) { 
                  XXFNCN = DSSDAP.getYUFNCN();
               } else {
                  XXFNCN = 1;
               }
               break;
            }
            XXFEID.move("AP50");
            XXFNCN = 1;
            break;
      }
      //   Set Accounting event
      switch (0) {
         default:
            if  (APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
               //   Pre payment Pre invoice
               PXCRTVAC.PPEVEN.move("AP53");
               break;
            }
            if  (APIBH.getIBTP().EQ("06")) {
               //   Pre payment Final invoice
               PXCRTVAC.PPEVEN.move("AP54");
               break;
            }
            if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) ||
                APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
               //   Debit note
               XXEVEN.move("AP51");
               break;
            }
            XXEVEN.move("AP50");
            break;
      }
   }

   public void getFamFunction() {
      //   Read FAM function
      FEFEID.move(XXFEID);
      FEFNCN.move(XXFNCN);
      found_CSYTAB_FFNC = cRefFFNCext.getCSYTAB_FFNC(SYTAB, found_CSYTAB_FFNC, APIBH.getCONO(), APIBH.getDIVI(), FESTKY);
      cRefFFNCext.setDSFFNC(SYTAB, DSFFNC);
      if (found_CSYTAB_FFNC) {
         automaticVATaccounting_AVAT = DSFFNC.getDFAVAT();
         cashDiscountMethod_CDGN = DSFFNC.getDFCDGN();
         voucherTextCode_VTXC = DSFFNC.getDFVTXC();
         notApprovedForPayment_PRDE = DSFFNC.getDFPRDE();
         if (isInvoiceClassFamFuncValid(XXFEID) && 	
             getInvoiceClass) { 	
            invoiceClass.moveLeftPad(DSFFNC.getDFIVCL());
         } 	
      }
      //   Set Approved/not approved for payment according to FAM-function
      switch (notApprovedForPayment_PRDE) {
         case 0:
            XXAPRV = 1;
            break;
         case 1:
            XXAPRV = 0;
            break;
         default:
            XXAPRV = 0;
            break;
      }
      currentVDSC.move(DSFFNC.getDFVDSC());
      currentVSER.move(DSFFNC.getDFVSER());
      // Check if overide with new values from CRS412
      PLRTVFNC.ETCONO = APIBH.getCONO(); 	 	
      PLRTVFNC.ETPGNM.moveLeftPad("APS455Sbm"); 	 	
      PLRTVFNC.ETDIVI.move(APIBH.getDIVI()); 	 	
      PLRTVFNC.ETFEID.move(XXFEID); 	 	
      PLRTVFNC.ETACDT = currentACDT; 	 	
      PLRTVFNC.ETCUNO.clear(); 	 	
      PLRTVFNC.ETSUNO.move(APIBH.getSUNO()); 	 	
      PLRTVFNC.ETFTCO.move(APIBH.getFTCO()); 	 	
      PLRTVFNC.ETBSCD.move(APIBH.getBSCD()); 	 	
      PLRTVFNC.ETECAR = XXECAR ; 	 	
      PLRTVFNC.ETEUVT = XXEUVT; 	 	
      PLRTVFNC.ETFOTA = 0; 	 	
      PLRTVFNC.ETCRED = 0; 	 	
      if (greaterOrEquals(APIBH.getCUAM(), cRefCUAM.decimals(), 0d)){ 	
         PLRTVFNC.ETCRED = 0; 	 	
      } else { 	
         PLRTVFNC.ETCRED = 1; 	 	
      } 	
      PLRTVFNC.CRTVFNC(); 	 	
      if (PLRTVFNC.ETFOTA == 1) { 	 	
         // Set values from FAM function exeptions 	 	
         if(currentVSER.isBlank()){ 	
            currentVSER.moveLeft(PLRTVFNC.ETVSER); 	 	
         } 	
         currentVDSC.moveLeft(PLRTVFNC.ETVDSC); 	 	
         voucherTextCode_VTXC = PLRTVFNC.ETVTXC; 	 	
         cashDiscountMethod_CDGN = PLRTVFNC.ETCDGN;
         notApprovedForPayment_PRDE = PLRTVFNC.ETPRDE;
         if(PLRTVFNC.ETPRDE == 0){ 	
            XXAPRV = 1; 	
         } else{ 	
            XXAPRV = 0; 	
         } 	
         automaticVATaccounting_AVAT = PLRTVFNC.ETAVAT; 	 	
         if (isInvoiceClassFamFuncValid(XXFEID) &&
             getInvoiceClass) {
            invoiceClass.moveLeftPad(PLRTVFNC.ETIVCL);
         }
      }
   }

   /**
   * Check if entered FAM function is valid for exception for invoice Calss
   */
   public boolean isInvoiceClassFamFuncValid(MvxString famFunction) {
      if (famFunction.NE("AP50") &&
          famFunction.NE("AP51") &&
          famFunction.NE("AP52")) {
         return false;
      }
      return true;
   }

   public void retrieveVoucherNumber() {
      //   Retrieve voucher number
      PLCHKVO.FVCONO = APIBH.getCONO();
      PLCHKVO.FVCMTP = LDAZD.CMTP;
      PLCHKVO.FVDIVI.move(APIBH.getDIVI());
      PLCHKVO.FVACBC = CRS750DS.getPBACBC();
      PLCHKVO.FVVSER.move(currentVSER);
      PLCHKVO.FVFEID.move(XXFEID);
      PLCHKVO.FVFETC = 0;
      PLCHKVO.FVVONI = 0;
      PLCHKVO.FVVTST = 0;
      PLCHKVO.FVACDT = currentACDT;
      PLCHKVO.FVYEA4 = (int)(currentACYP/100);
      IN92 = PLCHKVO.CCHKVON();
      if (PLCHKVO.FVMVMA == 2) {
         // Automatic voucher number
         PLCHKVO.FVFETC = 1;
         IN92 = PLCHKVO.CCHKVON();
         currentVONO = PLCHKVO.FVVONO;
      } else {
         // Manual voucher number
         currentVONO = APIBH.getVONO();
      }
   }

   /**
   * Initiate fields in FCR040 for this Invoice batch number 
   */
   public void initFieldsInFCR040() {
      //  Get key to FCR040
      CR040.clearNOKEY("10");
      GLS040_BJNO.moveLeftPad(this.getBJNO());
      CR040.setJBNO(GLS040_JBNO.getInt());
      CR040.setJBDT(GLS040_JBDT.getInt());
      CR040.setJBTM(GLS040_JBTM.getInt());
      CR040.setYEA4((int)(currentACYP/100));
      if (CRS750DS.getPBACBC() == 1) {
         CR040.setVSER().move(currentVSER);
      } else {
         CR040.setVSER().clear();
      }
      CR040.setVONO(currentVONO);
      CR040.setTRNO(0);
      //   Indicates that the invoice have been scanned into the system
      if (APIBH.getENME() == 1) {
         // Scanned invoice
         CR040.setENME(1);
      } else {
         CR040.setENME(0);
      }
      //   Init general fields for FCR040
      CR040.setCONO(APIBH.getCONO());
      CR040.setDIVI().move(APIBH.getDIVI());
      CR040.setBCHN(0);
      CR040.setCNNO(0);
      CR040.setTRNO(0);
      XXTRNO = 0;
      CR040.setACDT(currentACDT);
      CR040.setACYP(currentACYP);
      CR040.setOCDT(currentACDT);
      X1LCDC = LDAZD.LCDC;
      found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, APIBH.getCONO(), APIBH.getCUCD());
      if (found_CSYTAB_CUCD) {
         cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
         X1LCDC = DSCUCD.getYQDCCD();
      }
      CR040.setDCAM(X1LCDC);
      CR040.setFEID().move(XXFEID);
      CR040.setFNCN(XXFNCN);
      CR040.setVDSC().move(currentVDSC);
      CR040.setVTXT().move(APS455DS.getZWVTXT());
      switch (0) {
         default:
            if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE()) || APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE_REQUEST())) {
               //   Debit note
               CR040.setTDSC().move(DSSDAP.getYUTDSC());
               GINDH.setCONO(APIBH.getCONO());
               GINDH.setDIVI().move(APIBH.getDIVI());
               GINDH.setSUNO().move(APIBH.getSUNO());
               GINDH.setDNNR().move(APIBH.getSINO());
               if (GINDH.CHAIN_LOCK("00", GINDH.getKey("00", 4))) {
                  GINDH.setDNSS().move("99");
                  GINDH.setLMDT(movexDate());
                  GINDH.setCHID().move(this.DSUSS);
                  GINDH.setCHNO(GINDH.getCHNO() + 1);
                  GINDH.UPDAT("00");
                  XXDNOI.move(GINDH.getDNOI());
               } else {
                  XXDNOI.clear();
               }
               break;
            }
            CR040.setTDSC().clear();
            XXDNOI.clear();
            break;
      }
      CR040.setRGTM(movexTime());
      CR040.setLMDT(CR040.getRGDT());
      CR040.setCHNO(1);
      CR040.setCHID().move(this.DSUSS);
      CR040.setPGNM().moveLeftPad("APS450");
   }

   /**
   * Update FCR040 with invoice transaction
   */
   public void INVADD() {
      XXTRNO++;
      CR040.setTRNO(XXTRNO);
      XTACAM = 0d;
      XTCUAM = 0d;
      //  Cleare some fields in FCR040
      CLR04();
      CR040.setCONO(APIBH.getCONO());
      CR040.setDIVI().move(APIBH.getDIVI());
      CR040.setINBN(APIBH.getINBN());
      currentBCHN++;
      CR040.setBCHN(currentBCHN);
      CR040.setCNNO(0);
      CR040.setTRCD(40);
      CR040.setAPRV(XXAPRV);
      CR040.setAIT1().clear();
      CR040.setAIT2().clear();
      CR040.setAIT3().clear();
      CR040.setAIT4().clear();
      CR040.setAIT5().clear();
      CR040.setAIT6().clear();
      CR040.setAIT7().clear();
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      //    Set params for CRTVACC (Entry of inv-Accounts payable)
      PXCRTVAC.PPCONO = APIBH.getCONO();
      PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
      PXCRTVAC.PPEVEN.move(XXEVEN);
      PXCRTVAC.PPACTY.move("200 ");
      // Prepayment  
      if  (isPrePaymentActivated() && 
         APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE()) ||
         isPrePaymentActivated() && 
         APIBH.getIBTP().EQ("06")) {
         if  (APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
            if (!APIBH.getPPYN().isBlank()) { 
               // With Payment req number
               retrieveAccountingWithPaymentRequestNumber();                 
            } else {
               // Without Payment req number
               retrieveAccountingWithoutPaymentRequestNumber();                  
            }
         }
         if  (APIBH.getIBTP().EQ("06")) {
            // Without Payment req number
            retrieveAccountingWithoutPaymentRequestNumber();                  
         }
      } else {
         PXCRTVAC.PPFDAT = currentACDT;
         PXCRTVAC.PPCMTP = LDAZD.CMTP;
         PXCRTVAC.PPLANC.move(LDAZD.LANC);
         PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
         if (!APIBH.getSPYN().isBlank()) {
            PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
         } else {
            PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
         }
         //    Create accounting references
         IN92 = PXCRTVAC.CRTVACC();
         CR040.setAIT1().move(PXCRTVAC.PPAIT1);
         CR040.setAIT2().move(PXCRTVAC.PPAIT2);
         CR040.setAIT3().move(PXCRTVAC.PPAIT3);
         CR040.setAIT4().move(PXCRTVAC.PPAIT4);
         CR040.setAIT5().move(PXCRTVAC.PPAIT5);
         CR040.setAIT6().move(PXCRTVAC.PPAIT6);
         CR040.setAIT7().move(PXCRTVAC.PPAIT7);
      }
      CR040.setVTXT().move(APS455DS.getZWVTXT());
      CR040.setIVCL().moveLeftPad(invoiceClass);
      rXXRE20SyncTo();
      if (!XXRE20.isBlank()) {
         switch (0) {
            default:
               if (CR040.getEXN1() == 0 && CR040.getEXI1().isBlank()) {
                  //   Put reference field as extra information
                  CR040.setEXN1(402);
                  rXXRE20SyncTo();
                  CR040.setEXI1().move(XXRE20);
                  break;
               }
               if (CR040.getEXN2() == 0 && CR040.getEXI2().isBlank()) {
                  CR040.setEXN2(402);
                  rXXRE20SyncTo();
                  CR040.setEXI2().move(XXRE20);
                  break;
               }
               if (CR040.getEXN3() == 0 && CR040.getEXI3().isBlank()) {
                  CR040.setEXN3(402);
                  rXXRE20SyncTo();
                  CR040.setEXI3().move(XXRE20);
                  break;
               }
               if (CR040.getEXN4() == 0 && CR040.getEXI4().isBlank()) {
                  CR040.setEXN4(402);
                  rXXRE20SyncTo();
                  CR040.setEXI4().move(XXRE20);
                  break;
               }
               if (CR040.getEXN5() == 0 && CR040.getEXI5().isBlank()) {
                  CR040.setEXN5(402);
                  rXXRE20SyncTo();
                  CR040.setEXI5().move(XXRE20);
                  break;
               }
         }
      }
      //   Put Original invoice number for Debit note as extra information
      if (!XXDNOI.isBlank()) {
         switch (0) {
            default:
               if (CR040.getEXN1() == 0 && CR040.getEXI1().isBlank()) {
                  CR040.setEXN1(419);
                  CR040.setEXI1().moveLeft(XXDNOI);
                  break;
               }
               if (CR040.getEXN2() == 0 && CR040.getEXI2().isBlank()) {
                  CR040.setEXN2(419);
                  CR040.setEXI2().moveLeft(XXDNOI);
                  break;
               }
               if (CR040.getEXN3() == 0 && CR040.getEXI3().isBlank()) {
                  CR040.setEXN3(419);
                  CR040.setEXI3().moveLeft(XXDNOI);
                  break;
               }
               if (CR040.getEXN4() == 0 && CR040.getEXI4().isBlank()) {
                  CR040.setEXN4(419);
                  CR040.setEXI4().moveLeft(XXDNOI);
                  break;
               }
               if (CR040.getEXN5() == 0 && CR040.getEXI5().isBlank()) {
                  CR040.setEXN5(419);
                  CR040.setEXI5().moveLeft(XXDNOI);
                  break;
               }
         }
      }
      CR040.setDCQT(2);
      CR040.setDCAM(X1LCDC);
      //   - Fetch amount in local currency
      if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
          APIBH.getDIVI().EQ(LDAZD.DIVI) ||
          APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
          APIBH.getDIVI().NE(LDAZD.DIVI)) {
         PLCLCCU.FZCONO = APIBH.getCONO();
         PLCLCCU.FZDIVI.move(APIBH.getDIVI());
         if (APIBH.getDIVI().EQ(LDAZD.DIVI)) {
            PLCLCCU.FZDMCU = LDAZD.DMCU;
         } else {
            PLCLCCU.FZDMCU = MNDIV.getDMCU();
         }
         PLCLCCU.FZCRTP = APIBH.getCRTP();
         PLCLCCU.FZCUTD = APIBH.getIVDT();
         PLCLCCU.FZARAT = APIBH.getARAT();
         PLCLCCU.FZRAFA = 0;
         if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
             APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
            PLCLCCU.FZCUAM = -(APIBH.getADAB());
         } else {
            PLCLCCU.FZCUAM = -(APIBH.getCUAM());
         }
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZTEST = 0;
         PLCLCCU.FZCMTP = LDAZD.CMTP;
         PLCLCCU.FZVERR = 0;
         PLCLCCU.FZMSGN = 0;
         PLCLCCU.FZCUCD.move(APIBH.getCUCD());
         PLCLCCU.FZMSGI.clear();
         PLCLCCU.FZMSGA.clear();
         PLCLCCU.CCLCCUR();
         CR040.setACAM(PLCLCCU.FZACAM);
      } else {
         if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
             APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
            CR040.setACAM(-(APIBH.getADAB()));
         } else {
            CR040.setACAM(-(APIBH.getCUAM()));
         }
         PLCLCCU.FZCONO = APIBH.getCONO();
         PLCLCCU.FZDIVI.move(APIBH.getDIVI());
         if (APIBH.getDIVI().EQ(LDAZD.DIVI)) {
            PLCLCCU.FZDMCU = LDAZD.DMCU;
         } else {
            PLCLCCU.FZDMCU = MNDIV.getDMCU();
         }
         PLCLCCU.FZCUCD.move(APIBH.getCUCD());
         PLCLCCU.FZCRTP = APIBH.getCRTP();
         PLCLCCU.FZCUTD = APIBH.getIVDT();
         PLCLCCU.FZARAT = APIBH.getARAT();
         PLCLCCU.FZRAFA = 0;
         PLCLCCU.FZCUAM = 0d;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZTEST = 0;
         PLCLCCU.FZCMTP = LDAZD.CMTP;
         PLCLCCU.FZDCAM = 0;
         PLCLCCU.FZLCDC = 0;
         PLCLCCU.FZTX15.clear();
         PLCLCCU.FZVERR = 0;
         PLCLCCU.FZMSGI.clear();
         PLCLCCU.FZMSGN = 0;
         PLCLCCU.FZMSGA.clear();
         IN92 = PLCLCCU.CCLCCUR();
      }
      CR040.setCUCD().move(APIBH.getCUCD());
      CR040.setCRTP(APIBH.getCRTP());
      if (!APIBH.getFECN().isBlank()) {
         switch (0) {
            default:
               if (CR040.getEXN1() == 0 && CR040.getEXI1().isBlank()) {
                  //   Put future rate agreement as extra information
                  CR040.setEXN1(401);
                  CR040.setEXI1().moveLeft(APIBH.getFECN());
                  break;
               }
               if (CR040.getEXN2() == 0 && CR040.getEXI2().isBlank()) {
                  CR040.setEXN2(401);
                  CR040.setEXI2().moveLeft(APIBH.getFECN());
                  break;
               }
               if (CR040.getEXN3() == 0 && CR040.getEXI3().isBlank()) {
                  CR040.setEXN3(401);
                  CR040.setEXI3().moveLeft(APIBH.getFECN());
                  break;
               }
               if (CR040.getEXN4() == 0 && CR040.getEXI4().isBlank()) {
                  CR040.setEXN4(401);
                  CR040.setEXI4().moveLeft(APIBH.getFECN());
                  break;
               }
               if (CR040.getEXN5() == 0 && CR040.getEXI5().isBlank()) {
                  CR040.setEXN5(401);
                  CR040.setEXI5().moveLeft(APIBH.getFECN());
                  break;
               }
         }
      }
      // Prepayment
      if  (isPrePaymentActivated() && APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {    
         CR040.setIVTP().moveLeftPad("PI");
         if (!hasExtraInfoNumber(450)) {
            addExtraInfo(450, APIBH.getPPYR());
         }
         if (!hasExtraInfoNumber(50)) {
            addExtraInfo(50, APIBH.getPPYR());
         }
         if(APIBH.getYEA4() != 0) {
            if (!hasExtraInfoNumber(452)) {
               alphaYEA4.move(APIBH.getYEA4());
               addExtraInfo(452, alphaYEA4);
            }
            if (!hasExtraInfoNumber(52)) {
               alphaYEA4.move(APIBH.getYEA4());
               addExtraInfo(52, alphaYEA4);
            }
         }
         if(!APIBH.getPPYN().isBlank()) {
            if (!hasExtraInfoNumber(451)) {
               addExtraInfo(451, APIBH.getPPYN());
            }  
            if (!hasExtraInfoNumber(51)) {
               addExtraInfo(51, APIBH.getPPYN());
            } 
            CR040.setRECO(9);
            CR040.setREDE(movexDate()); 
         }
      }
      // Final invoice
      if  (isPrePaymentActivated() && APIBH.getIBTP().EQ("06")) {    
         CR040.setIVTP().moveLeftPad("PF");
         if (!hasExtraInfoNumber(450)) {
            addExtraInfo(450, APIBH.getPPYR());
         }
         if (!hasExtraInfoNumber(50)) {
            addExtraInfo(50, APIBH.getPPYR());
         }
      }
      CR040.setTECD().move(APIBH.getTECD());
      CR040.setARAT(PLCLCCU.FZARAT);      
      if (APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM()) ||
          APIBH.getIBTP().EQ(cRefIBTPext.SUPPLIER_CLAIM_REQUEST())) {
         CR040.setCUAM(-(APIBH.getADAB()));
      } else {
         CR040.setCUAM(-(APIBH.getCUAM()));
      }
      CR040.setACDT(currentACDT);
      CR040.setYEA4((int)(currentACYP/100));
      CR040.setACYP(currentACYP);
      CR040.setAPCD().move(APIBH.getAPCD());
      // Read CIDVEN / CSUDIV
      foundParam_CIDVEN.moveLeft(toChar(found_CIDVEN));
      foundParam_CSUDIV.moveLeft(toChar(found_CSUDIV));
      cRefSUNOext.getCIDVEN_CSUDIV(IDVEN, SUDIV, foundParam_CIDVEN, foundParam_CSUDIV, APIBH.getCONO(), APIBH.getDIVI(),  APIBH.getSUNO());
      found_CIDVEN = foundParam_CIDVEN.getBoolean();
      found_CSUDIV = foundParam_CSUDIV.getBoolean();
      if (found_CIDVEN) {
         CR040.setSUCL().move(IDVEN.getSUCL());
      }
      CR040.setSUNO().move(APIBH.getSUNO());
      CR040.setSPYN().move(APIBH.getSPYN());
      if (APIBH.getSPYN().isBlank()) {
         CR040.setSPYN().move(APIBH.getSUNO());
      }
      CR040.setSERS(APIBH.getSERS());
      CR040.setSINO().move(APIBH.getSINO());
      CR040.setIVDT(APIBH.getIVDT());
      CR040.setDUDT(APIBH.getDUDT());
      CR040.setVTCD(IDVEN.getVTCD());
      if (!APIBH.getFTCO().isBlank()) {
         CR040.setFTCO().move(APIBH.getFTCO());
      } else {
         CR040.setFTCO().move(IDMAS.getCSCD());
      }
      if (!APIBH.getBSCD().isBlank()) {
         CR040.setBSCD().move(APIBH.getBSCD());
      } else {
         CR040.setBSCD().move(MNDIV.getCSCD());
      }
      CR040.setTDCD().move(APIBH.getTDCD());
      CR040.setVTAM(-(0d));
      CR040.setRGDT(movexDate());
      XRDATE = APIBH.getIVDT();
      RETYEA();
      CR040.setINYR(XPYEA4);
      CR040.setLMDT(CR040.getRGDT());
      CR040.setCHID().move(this.DSUSS);
      CR040.setRGTM(movexTime());
      CR040.setDBCR(' ');
      if (CRS750DS.getPBDCNY() == 1) {
         if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
            CR040.setDBCR(CRS750DS.getPBDBNG());
         } else {
            CR040.setDBCR(CRS750DS.getPBDBPS());
         }
      }
      CR040.setTEMP(0);
      CR040.setVTXC(voucherTextCode_VTXC); 	
      if (CRS750DS.getPBACBC() == 1) {
         CR040.setVSER().move(currentVSER);
      } else {
         CR040.setVSER().clear();
      }
      PLCHKVO.FVFEID.move(XXFEID);
      CR040.setFNCN(XXFNCN);
      CR040.setPYME().move(APIBH.getPYME());
      //   Get payment type
      found_CSYTAB_PYME = cRefPYMEext.getCSYTAB_PYME(SYTAB, found_CSYTAB_PYME, APIBH.getCONO(), CR040.getPYME());
      cRefPYMEext.setDSPYME(SYTAB, DSPYME);
      CR040.setPYTP().move(DSPYME.getFRPYTP());
      CR040.setTEPY().move(APIBH.getTEPY());
      //    Entry method
      if (APIBH.getENME() == 1) {
         // Scanned invoice
         CR040.setENME(1);
      } else {
         CR040.setENME(0);
      }
      if (!DSPYME.getFRPYTP().isBlank()) { 	 	 	 	 	
         paymentClass = getPaymentClass(DSPYME.getFRPYTP()); 	 	 	 	 	
      } 	 	 	 	 	
      CR040.setBKID().moveLeftPad(APIBH.getBKID());
      ADAT04();
      if (CR040.getVTXT().isBlank()) {
         XXSUNO.move(CR040.getSUNO());
         XXSINO.moveLeft(CR040.getSINO());
         XXINYR.move(CR040.getINYR());
         XXALSU.moveLeft(IDMAS.getALSU());
         CR040.setVTXT().moveLeft(XXVTXT);
      }
      // Sales tax
      if (MNDIV.getTATM() == 2) { 
         CR040.setGEOC(APIBH.getGEOC());
         CR040.setTXIN(APIBH.getTXIN());         
      }
      //   Accumulate for test of unbalanced voucher
      XTACAM += CR040.getACAM();
      XTCUAM += CR040.getCUAM();
      // If this is a Corrective invoice we must add 	
      // an additional info record with original invoice no 	
      if (!APIBH.getDNOI().isBlank()) {
         XXInvoice.moveLeftPad(APIBH.getDNOI());
         XXInvYear.move(APIBH.getOYEA());
         // Read FPLEDG to get original invoice, Year, Journal, Sequence no 	
         PLEDG.setCONO(APIBH.getCONO()); 	
         PLEDG.setDIVI().move(APIBH.getDIVI()); 	
         PLEDG.setSINO().move(APIBH.getDNOI()); 	
         PLEDG.setINYR(APIBH.getOYEA()); 	
         IN91 = !PLEDG.CHAIN("39", PLEDG.getKey("39", 4));
         if (!IN91) { 	
             original_YEA4 = PLEDG.getYEA4(); 	
             original_JRNO = PLEDG.getJRNO(); 	
             original_JSNO = PLEDG.getJSNO(); 	
         } 	
         switch (0) { 	
            default: 	
               if (CR040.getEXN1() == 0 && CR040.getEXI1().isBlank()) { 	
                  CR040.setEXN1(435); 	
                  CR040.setEXI1().moveLeft(XXInvInfo); 	
                  break; 	
               } 	
               if (CR040.getEXN2() == 0 && CR040.getEXI2().isBlank()) { 	
                  CR040.setEXN2(435); 	
                  CR040.setEXI2().moveLeft(XXInvInfo); 	
                  break; 	
               } 	
               if (CR040.getEXN3() == 0 && CR040.getEXI3().isBlank()) { 	
                  CR040.setEXN3(435); 	
                  CR040.setEXI3().moveLeft(XXInvInfo); 	
                  break; 	
               } 	
               if (CR040.getEXN4() == 0 && CR040.getEXI4().isBlank()) { 	
                  CR040.setEXN4(435); 	
                  CR040.setEXI4().moveLeft(XXInvInfo); 	
                  break; 	
               } 	
               if (CR040.getEXN5() == 0 && CR040.getEXI5().isBlank()) { 	
                  CR040.setEXN5(435); 	
                  CR040.setEXI5().moveLeft(XXInvInfo); 	
                  break; 	
               } 	
         } 	
         // Check if any previous corrective exist on this original 	
         checkPreviousCorrective(); 	
         if (!previousCorr.isBlank()) { 	
            XXInvoice.moveLeft(previousCorr); 	
            XXInvYear.move(previousInyr); 	
            switch (0) { 	
               default: 	
                  if (CR040.getEXN1() == 0 && CR040.getEXI1().isBlank()) { 	
                     CR040.setEXN1(437); 	
                     CR040.setEXI1().moveLeft(XXInvInfo); 	
                     break; 	
                  } 	
                  if (CR040.getEXN2() == 0 && CR040.getEXI2().isBlank()) { 	
                     CR040.setEXN2(437); 	
                     CR040.setEXI2().moveLeft(XXInvInfo); 	
                     break; 	
                  } 	
                  if (CR040.getEXN3() == 0 && CR040.getEXI3().isBlank()) { 	
                     CR040.setEXN3(437); 	
                     CR040.setEXI3().moveLeft(XXInvInfo); 	
                     break; 	
                  } 	
                  if (CR040.getEXN4() == 0 && CR040.getEXI4().isBlank()) { 	
                     CR040.setEXN4(437); 	
                     CR040.setEXI4().moveLeft(XXInvInfo); 	
                     break; 	
                  } 	
                  if (CR040.getEXN5() == 0 && CR040.getEXI5().isBlank()) { 	
                     CR040.setEXN5(437); 	
                     CR040.setEXI5().moveLeft(XXInvInfo); 	
                     break; 	
                  } 	
            } 	
         } 	
         // Update additional info 436 on the Original invoice 	
         updateOriginalInvoice(); 	
      }
      CR040.WRITE("00");
   }

   /**
   * Update FCR040 with Claim adjustment
   */
   public void claimAdjustment() {
      //  Clear some fields in FCR040
      CLR04();
      // Read all invoice lines and validate
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().moveLeftPad(APIBH.getDIVI());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.setRDTP(cRefRDTPext.CLAIM_LINE());
      APIBL.SETLL("10", APIBL.getKey("10", 4));
      while (APIBL.READE("10", APIBL.getKey("10", 4))) {
         if (!isBlank((APIBL.getNLAM() - APIBL.getADAB()), cRefCUAM.decimals())) {
            XXTRNO++;
            CR040.setTRNO(XXTRNO);
            if (!CR040.CHAIN_LOCK("00", CR040.getKey("00"))) {
               CR040.setTRCD(41);
               CR040.setAPRV(XXAPRV);
               //    Set params for CRTVACC (Supplier invoice batch-Claim adjustment)
               PXCRTVAC.PPCONO = APIBH.getCONO();
               PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
               PXCRTVAC.PPEVEN.move(XXEVEN);
               PXCRTVAC.PPACTY.move("440 ");
               PXCRTVAC.PPFDAT = currentACDT;
               PXCRTVAC.PPCMTP = LDAZD.CMTP;
               PXCRTVAC.PPLANC.move(LDAZD.LANC);
               PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
               if (!APIBH.getSPYN().isBlank()) {
                  PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
               } else {
                  PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
               }
               //    Create accounting references
               IN92 = PXCRTVAC.CRTVACC();
               CR040.setAIT1().move(PXCRTVAC.PPAIT1);
               CR040.setAIT2().move(PXCRTVAC.PPAIT2);
               CR040.setAIT3().move(PXCRTVAC.PPAIT3);
               CR040.setAIT4().move(PXCRTVAC.PPAIT4);
               CR040.setAIT5().move(PXCRTVAC.PPAIT5);
               CR040.setAIT6().move(PXCRTVAC.PPAIT6);
               CR040.setAIT7().move(PXCRTVAC.PPAIT7);
               //   - Fetch amount in local currency
               if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                   APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                   APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                   APIBH.getDIVI().NE(LDAZD.DIVI)) {
                  PLCLCCU.FZCONO = APIBH.getCONO();
                  PLCLCCU.FZDIVI.move(APIBH.getDIVI());
                  if (APIBH.getDIVI().EQ(LDAZD.DIVI)) {
                     PLCLCCU.FZDMCU = LDAZD.DMCU;
                  } else {
                     PLCLCCU.FZDMCU = MNDIV.getDMCU();
                  }
                  PLCLCCU.FZCRTP = APIBH.getCRTP();
                  PLCLCCU.FZCUTD = APIBH.getIVDT();
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = -(APIBL.getNLAM() - APIBL.getADAB());
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZCMTP = LDAZD.CMTP;
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZCUCD.move(APIBH.getCUCD());
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGA.clear();
                  PLCLCCU.CCLCCUR();
                  CR040.setACAM(PLCLCCU.FZACAM);
               } else {
                  CR040.setACAM(-(APIBL.getNLAM() - APIBL.getADAB()));
                  PLCLCCU.FZCONO = APIBH.getCONO();
                  PLCLCCU.FZDIVI.move(APIBH.getDIVI());
                  if (APIBH.getDIVI().EQ(LDAZD.DIVI)) {
                     PLCLCCU.FZDMCU = LDAZD.DMCU;
                  } else {
                     PLCLCCU.FZDMCU = MNDIV.getDMCU();
                  }
                  PLCLCCU.FZCUCD.move(APIBH.getCUCD());
                  PLCLCCU.FZCRTP = APIBH.getCRTP();
                  PLCLCCU.FZCUTD = APIBH.getIVDT();
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = 0d;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZCMTP = LDAZD.CMTP;
                  PLCLCCU.FZDCAM = 0;
                  PLCLCCU.FZLCDC = 0;
                  PLCLCCU.FZTX15.clear();
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZMSGA.clear();
                  IN92 = PLCLCCU.CCLCCUR();
               }
               CR040.setCUAM(-(APIBL.getNLAM() - APIBL.getADAB()));
               CR040.setARAT(PLCLCCU.FZARAT);
               CR040.setVTCD(APIBL.getVTCD());
               CR040.setVTAM(0d);
               CR040.setDBCR(' ');
               if (CRS750DS.getPBDCNY() == 1) {
                  if (lessThan(CR040.getACAM(), cRefACAM.decimals(), 0d)) {
                     CR040.setDBCR(CRS750DS.getPBDBNG());
                  } else {
                     CR040.setDBCR(CRS750DS.getPBDBPS());
                  }
               }
               ADAT04();
               CR040.setCVT1(0d);
               CR040.setCVT2(0d);
               if (CR040.getAT04() >= 3 &&
                   CR040.getAT04() <= 9 &&
                   CR040.getVTCD() != 0) {
                  PLCRTVT.FTCONO = APIBH.getCONO();
                  PLCRTVT.FTDIVI.move(APIBH.getDIVI());
                  PLCRTVT.FTCMTP = LDAZD.CMTP;
                  PLCRTVT.FTTASK = 1;
                  PLCRTVT.FTVATH = 1;
                  PLCRTVT.FTVTCD = CR040.getVTCD();
                  PLCRTVT.FTACDT = APIBH.getIVDT();
                  PLCRTVT.FTLCDC = X1LCDC;
                  PLCRTVT.FTBAAM = CR040.getCUAM();
                  PLCRTVT.FTCUCD.move(APIBH.getCUCD());
                  PLCRTVT.FTEVEN.move(XXEVEN);
                  PLCRTVT.FTTECD.move(APIBH.getTECD());
                  if (!APIBH.getSPYN().isBlank()) {
                     PLCRTVT.FTSUNO.move(APIBH.getSPYN());
                  } else {
                     PLCRTVT.FTSUNO.move(APIBH.getSUNO());
                  }
                  PLCRTVT.FTCUNO.clear();
                  PLCRTVT.FTCSCD.move(XXBSCD);
                  PLCRTVT.FTECAR.move(XXECAR);
                  if (XXEUVT == 1) {
                     PLCRTVT.FTIOCD = 3;
                  } else {
                     PLCRTVT.FTIOCD = 2;
                     sendFromToCountryAsInput();
                  }
                  PLCRTVT.FTAI11.clear();
                  XVECAR.move(XXECAR);
                  XVFTCO.move(XXFTCO);
                  XVBSCD.move(XXBSCD);
                  PLCRTVT.FTAI11.moveLeft(XVCFI1);
                  PLCRTVT.CCRTVAT();
               } else {
                  PLCRTVT.FTVTM1 = 0D;
                  PLCRTVT.FTVTM2 = 0D;
               }
               if (LDAZD.TATM == 1 ||
                   LDAZD.TATM == 4) { 	
                  CR040.setCVT1(PLCRTVT.FTVTM1);
                  CR040.setCVT2(PLCRTVT.FTVTM2);
               }
               XTACAM += CR040.getACAM();
               XTCUAM += CR040.getCUAM();
               CR040.setIVCL().moveLeftPad(invoiceClass);
               CR040.WRITE("00");
            } else {
               CR040.UNLOCK("00");
            }
         }
      }
   }

   /**
   * Retrieve year for a date
   */
   public void RETYEA() {
      //     * Retrieve year (YEA4)
      this.PXDIVI.move(CR040.getDIVI());
      this.PXDFMI.move("YMD8");
      this.PXDATI = XRDATE;
      this.PXDFMO.move("YMD8");
      this.PXOPRM = 0;
      COMDAT();
      switch (XXPTFA) {
         case 1:
            XPYEA4 = (int)(this.PXCYP1/100);
            break;
         case 2:
            XPYEA4 = (int)(this.PXCYP2/100);
            break;
         case 3:
            XPYEA4 = (int)(this.PXCYP3/100);
            break;
         case 4:
            XPYEA4 = (int)(this.PXCYP4/100);
            break;
         case 5:
            XPYEA4 = (int)(this.PXCYP5/100);
            break;
      }
   }

   /**
   * Update FCR040 with EAAT04
   */
   public void ADAT04() {
      found_FCHACC = cRefAITMext.getFCHACC(CHACC, found_FCHACC, LDAZD.CMTP, APIBH.getCONO(), CR040.getDIVI(), 1, CR040.getAIT1());
      if (found_FCHACC) {
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setAT04(CHACC.getAT04());
         } else {
            CR040.setAT04(0);
         }
         if (CHACC.getACR2() == 5) {
            CR040.setAIT2().clear();
         }
         if (CHACC.getACR3() == 5) {
            CR040.setAIT3().clear();
         }
         if (CHACC.getACR4() == 5) {
            CR040.setAIT4().clear();
         }
         if (CHACC.getACR5() == 5) {
            CR040.setAIT5().clear();
         }
         if (CHACC.getACR6() == 5) {
            CR040.setAIT6().clear();
         }
         if (CHACC.getACR7() == 5) {
            CR040.setAIT7().clear();
         }
      }
   }

   /**
   * Update FSCASH with cash discount transactions
   */
   public void CDIADD() {
      if (cashDiscountMethod_CDGN == 2 && 	 	
         (LDAZD.TATM == 1 || LDAZD.TATM == 4)) { 	
         XXCDAM = APIBH.getCUAM() - APIBH.getVTAM();
      } else {
         XXCDAM = APIBH.getCUAM();
      }
      if (!isBlank(XXCDAM, cRefCDAM.decimals())) {
         if (!isBlank(APIBH.getCDP1(), cRefCDP1.decimals()) ||
             !isBlank(APIBH.getCDP2(), cRefCDP2.decimals()) ||
             !isBlank(APIBH.getCDP3(), cRefCDP3.decimals())) {
            PCASH.setJBNO(CR040.getJBNO());
            PCASH.setJBDT(CR040.getJBDT());
            PCASH.setJBTM(CR040.getJBTM());
            PCASH.setBCHN(CR040.getBCHN());
            PCASH.setTRNO(1);
            IN91 = !PCASH.CHAIN_LOCK("00", PCASH.getKey("00", 5));
            if (IN91) {
               PCASH.clearNOKEY("00");
            }
            PCASH.setCONO(APIBH.getCONO());
            PCASH.setDIVI().move(CR040.getDIVI());
            PCASH.setYEA4((int)(CR040.getACYP()/100));
            if (CRS750DS.getPBACBC() == 1) {
               PCASH.setVSER().move(currentVSER);
            } else {
               PCASH.setVSER().clear();
            }
            PCASH.setVONO(currentVONO);
            PCASH.setSINO().move(APIBH.getSINO());
            PCASH.setTECD().move(APIBH.getTECD());
            if(!isBlank(APIBH.getTASD(), cRefCDAM.decimals())){ 	 	
               PCASH.setCDAM(-(APIBH.getTASD())); 	 	
            } else { 	 	
               PCASH.setCDAM(-(XXCDAM));
            } 	
            PCASH.setCDP1(APIBH.getCDP1());
            PCASH.setCDP2(APIBH.getCDP2());
            PCASH.setCDP3(APIBH.getCDP3());
            PCASH.setCDT1(APIBH.getCDT1());
            PCASH.setCDT2(APIBH.getCDT2());
            PCASH.setCDT3(APIBH.getCDT3());
            if (!IN91) {
               PCASH.UPDAT("00");
            } else {
               PCASH.WRITE("00");
            }
         }
      }
   }

   /**
   * Update FCR040 with VAT transactions
   */
   public void VATADD() {
      //Cleare some fields in FCR040
      XAVTA1 = 0D;
      XAVTA2 = 0D;
      XCVTA1 = 0D;
      XCVTA2 = 0D;
      multipleVTCD = false; 	
      previousVTCD = 0; 	
      computeVAT = false; 	
      CLR04();
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().move(APIBH.getDIVI());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.setRDTP(cRefRDTPext.VAT());
      APIBL.SETLL("10", APIBL.getKey("10", 4));
      //   Read records
      IN93 = !APIBL.READE("10", APIBL.getKey("10", 4));
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      if (!IN93) {
         hasVATLine = true;
         // Record type 3 overrule EU VAT
         //   - Do for all VAT codes (=record type = 03)
         while (!IN93) {
            /* // GLAM must be reversed for credit invoices 	
            if (APIBH.getIVTP().EQ("02")) { 	
               APIBL.setGLAM(-(APIBL.getGLAM())); 	
            } 	*/
            if (!isBlank(APIBL.getVTA1(), cRefVTA1.decimals())) {               
               CR040.setTRCD(41);
               CR040.setAPRV(XXAPRV);
               computeVAT = true; 	
               if (previousVTCD != APIBL.getVTCD() && 	
                   previousVTCD != 0) { 	
                  multipleVTCD = true; 	
               }
               //   - Fetch amount in local currency
               if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                   APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                   APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                   APIBH.getDIVI().NE(LDAZD.DIVI)) {
                  PLCLCCU.FZCUAM = APIBL.getNLAM();
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.CCLCCUR();
                  XXACAM = PLCLCCU.FZACAM;
               } else {
                  XXACAM = APIBL.getNLAM();
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = 0d;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZDCAM = 0;
                  PLCLCCU.FZLCDC = 0;
                  PLCLCCU.FZTX15.clear();
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZMSGA.clear();
                  IN92 = PLCLCCU.CCLCCUR();
               }
               XXCUAM = APIBL.getNLAM();
               //   - Fetch amount in local currency
               if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                   APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                   APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                   APIBH.getDIVI().NE(LDAZD.DIVI)) {
                  PLCLCCU.FZCUAM = APIBL.getVTA1();
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.CCLCCUR();
                  XAVTA1 = PLCLCCU.FZACAM;
               } else {
                  XAVTA1 = APIBL.getVTA1();
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = 0d;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZDCAM = 0;
                  PLCLCCU.FZLCDC = 0;
                  PLCLCCU.FZTX15.clear();
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZMSGA.clear();
                  IN92 = PLCLCCU.CCLCCUR();
               }
               XCVTA1 = APIBL.getVTA1();
               if (!isBlank(APIBL.getVTA2(), EPS_2)) {                  
                  //   - Fetch amount in local currency
                  if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                      APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                      APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                      APIBH.getDIVI().NE(LDAZD.DIVI)) {
                     PLCLCCU.FZCUAM = APIBL.getVTA2();
                     PLCLCCU.FZACAM = 0d;
                     PLCLCCU.FZARAT = APIBH.getARAT();
                     PLCLCCU.CCLCCUR();
                     XAVTA2 = PLCLCCU.FZACAM;
                  } else {
                     XAVTA2 = APIBL.getVTA2();
                     PLCLCCU.FZARAT = APIBH.getARAT();
                     PLCLCCU.FZRAFA = 0;
                     PLCLCCU.FZCUAM = 0d;
                     PLCLCCU.FZACAM = 0d;
                     PLCLCCU.FZTEST = 0;
                     PLCLCCU.FZDCAM = 0;
                     PLCLCCU.FZLCDC = 0;
                     PLCLCCU.FZTX15.clear();
                     PLCLCCU.FZVERR = 0;
                     PLCLCCU.FZMSGI.clear();
                     PLCLCCU.FZMSGN = 0;
                     PLCLCCU.FZMSGA.clear();
                     IN92 = PLCLCCU.CCLCCUR();
                  }
                  XCVTA2 = APIBL.getVTA2();
               } else {
                  XAVTA2 = 0d;
                  XCVTA2 = 0d;
               }
               CR040.setFTCO().move(XXFTCO);
               CR040.setBSCD().move(XXBSCD);
               CR040.setVTCD(APIBL.getVTCD());
               PLCRTVT.FTCONO = APIBH.getCONO();
               PLCRTVT.FTDIVI.move(APIBL.getDIVI());
               PLCRTVT.FTCMTP = LDAZD.CMTP;
               PLCRTVT.FTTASK = 3;
               PLCRTVT.FTVATH = 1;
               PLCRTVT.FTVTCD = APIBL.getVTCD();
               PLCRTVT.FTACDT = APIBH.getIVDT();
               PLCRTVT.FTLCDC = X1LCDC;
               PLCRTVT.FTBAAM = -(APIBL.getNLAM());
               PLCRTVT.FTCUCD.move(APIBH.getCUCD());
               PLCRTVT.FTEVEN.move(XXEVEN);
               PLCRTVT.FTTECD.move(APIBH.getTECD());
               if (!APIBH.getSPYN().isBlank()) {
                  PLCRTVT.FTSUNO.move(APIBH.getSPYN());
               } else {
                  PLCRTVT.FTSUNO.move(APIBH.getSUNO());
               }
               PLCRTVT.FTCUNO.clear();
               PLCRTVT.FTCSCD.move(XXBSCD);
               PLCRTVT.FTECAR.move(XXECAR);
               if (XXEUVT == 1) {
                  PLCRTVT.FTIOCD = 3;
               } else {
                  PLCRTVT.FTIOCD = 2;
                  sendFromToCountryAsInput();
               }
               PLCRTVT.FTAI11.clear();
               XVECAR.move(XXECAR);
               XVFTCO.move(XXFTCO);
               XVBSCD.move(XXBSCD);
               PLCRTVT.FTAI11.moveLeft(XVCFI1);
               PLCRTVT.CCRTVAT();
               CR040.setVTP1(PLCRTVT.FTVTP1);
               CR040.setVTP2(PLCRTVT.FTVTP2);
               CR040.setVTAM(0d);
               CR040.setARAT(PLCLCCU.FZARAT);
               /* if (APIBH.getIVTP().EQ("02")) {
                  XXCUAM = -(XXCUAM);
                  XXACAM = -(XXACAM);
                  XAVTA1 = -(XAVTA1);
                  XAVTA2 = -(XAVTA2);
                  XCVTA1 = -(XCVTA1);
                  XCVTA2 = -(XCVTA2);
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     XXVTAM = -(XXVTAM);
                  }
               } */
               CR040.setAIT1().move(PLCRTVT.FTAI11);
               CR040.setAIT2().move(PLCRTVT.FTAI12);
               CR040.setAIT3().move(PLCRTVT.FTAI13);
               CR040.setAIT4().move(PLCRTVT.FTAI14);
               CR040.setAIT5().move(PLCRTVT.FTAI15);
               CR040.setAIT6().move(PLCRTVT.FTAI16);
               CR040.setAIT7().move(PLCRTVT.FTAI17);
               ADAT04();
               IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
               if (IN91) {
                  CR040.setCUAM(XCVTA1);
                  if (!isBlank(APIBL.getGLAM(), cRefGLAM.decimals())) {
                     CR040.setACAM(APIBL.getGLAM());
                  } else {
                     CR040.setACAM(XAVTA1);
                  }
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     CR040.setVTAM(XXVTAM);
                  } else {
                     CR040.setVTAM(0d);
                  }
                  CR040.setDEDA(APIBH.getDEDA());
                  CR040.setARAT(PLCLCCU.FZARAT);
                  XXTRNO++;
                  CR040.setTRNO(XXTRNO);
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  //    Entry method
                  if (APIBH.getENME() == 1) {
                     // Scanned invoice
                     CR040.setENME(1);
                  } else {
                     CR040.setENME(0);
                  }
                  //   Accumulate for test of unbalanced voucher
                  XTACAM += CR040.getACAM();
                  XTCUAM += CR040.getCUAM();
                  CR040.setIVCL().moveLeftPad(invoiceClass); 	
                  CR040.WRITE("10");
               } else {
                  CR040.setCUAM(CR040.getCUAM() + XCVTA1);
                  if (!isBlank(APIBL.getGLAM(), cRefGLAM.decimals())) {
                     CR040.setACAM(CR040.getACAM() + APIBL.getGLAM());
                  } else {
                     CR040.setACAM(CR040.getACAM() + XAVTA1);
                  }
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     CR040.setVTAM(CR040.getVTAM() + XXVTAM);
                  } else {
                     CR040.setVTAM(0d);
                  }
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  //   Accumulate for test of unbalanced voucher
                  if (!isBlank(APIBL.getGLAM(), cRefGLAM.decimals())) {
                     XTACAM += APIBL.getGLAM();
                  } else {
                     XTACAM += XAVTA1;
                  }
                  XTCUAM += XCVTA1;
                  CR040.UPDAT("10");
               }
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  XXVTAM = -(XXVTAM);
               }
               // No accounting of VTA2 if GLAM exists. It contains sum of VTA1 and VTA2
               if (!isBlank(APIBL.getVTA2(), cRefVTA2.decimals()) &&
                   isBlank(APIBL.getGLAM(), cRefGLAM.decimals()) &&
                   !market_RU) {
                  CR040.setAIT1().move(PLCRTVT.FTAI21);
                  CR040.setAIT2().move(PLCRTVT.FTAI22);
                  CR040.setAIT3().move(PLCRTVT.FTAI23);
                  CR040.setAIT4().move(PLCRTVT.FTAI24);
                  CR040.setAIT5().move(PLCRTVT.FTAI25);
                  CR040.setAIT6().move(PLCRTVT.FTAI26);
                  CR040.setAIT7().move(PLCRTVT.FTAI27);
                  ADAT04();
                  IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
                  if (IN91) {
                     CR040.setCUAM(XCVTA2);
                     if (!isBlank(APIBL.getGLAM(), cRefGLAM.decimals()) &&
                         !isBlank(XAVTA2, cRefVTA2.decimals())) {
                        CR040.setACAM(-(APIBL.getGLAM()));
                     } else {
                        CR040.setACAM(XAVTA2);
                     }
                     if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                        CR040.setVTAM(XXVTAM);
                     } else {
                        CR040.setVTAM(0d);
                     }
                     CR040.setDEDA(APIBH.getDEDA());
                     XXTRNO++;
                     CR040.setTRNO(XXTRNO);
                     CR040.setDBCR(' ');
                     if (CRS750DS.getPBDCNY() == 1) {
                        if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                           CR040.setDBCR(CRS750DS.getPBDBNG());
                        } else {
                           CR040.setDBCR(CRS750DS.getPBDBPS());
                        }
                     }
                     //    Entry method
                     //   Accumulate for test of unbalanced voucher
                     XTACAM += CR040.getACAM();
                     XTCUAM += CR040.getCUAM();
                     CR040.setIVCL().moveLeftPad(invoiceClass); 	
                     CR040.WRITE("10");
                  } else {
                     CR040.setCUAM(CR040.getCUAM() + XCVTA2);
                     CR040.setACAM(CR040.getACAM() + XAVTA2);
                     if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                        CR040.setVTAM(CR040.getVTAM() + XXVTAM);
                     } else {
                        CR040.setVTAM(0d);
                     }
                     CR040.setDBCR(' ');
                     if (CRS750DS.getPBDCNY() == 1) {
                        if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                           CR040.setDBCR(CRS750DS.getPBDBNG());
                        } else {
                           CR040.setDBCR(CRS750DS.getPBDBPS());
                        }
                     }
                     //   Accumulate for test of unbalanced voucher
                     XTACAM += XAVTA2;
                     XTCUAM += XCVTA2;
                     CR040.UPDAT("10");
                  }
               }
               // If Russian market create 2 more VAT records for type R21
               if (market_RU) {
                  createR21();
               }
            }
            if (CRS750DS.getPBVTPC() == 2 ||
                CRS750DS.getPBVTPC() == 3) {
               if (CR040.getCUCD().NE(XXLOCD) &&
                   !isBlank(APIBL.getGLAM(), cRefGLAM.decimals())) {
                  CURDEV();
               }
            }
            previousVTCD = APIBL.getVTCD(); 	
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            //   Read records
            IN93 = !APIBL.READE("10", APIBL.getKey("10", 4));
         }
      } else {
         hasVATLine = false;
         if (XXEUVT == 1 || VATT08 == 1) {
            CR040.setTRCD(41);
            CR040.setAPRV(XXAPRV);
            CR040.setFTCO().move(XXFTCO);
            CR040.setBSCD().move(XXBSCD);
            CR040.setVTCD(XXVTCD);
            PLCRTVT.FTCONO = APIBH.getCONO();
            PLCRTVT.FTDIVI.move(APIBH.getDIVI());
            PLCRTVT.FTCMTP = LDAZD.CMTP;
            PLCRTVT.FTTASK = 3;
            PLCRTVT.FTVATH = 1;
            PLCRTVT.FTVTCD = XXVTCD;
            PLCRTVT.FTACDT = APIBH.getIVDT();
            PLCRTVT.FTLCDC = X1LCDC;
            if (comingFromCLEADD) { 	 	
               comingFromCLEADD = false; 	 	
               PLCRTVT.FTBAAM = (X1CUAM); 	 	
            } else { 	 	
               PLCRTVT.FTBAAM = (APIBH.getCUAM());
            }
            PLCRTVT.FTCUCD.move(APIBH.getCUCD());
            PLCRTVT.FTEVEN.move(XXEVEN);
            PLCRTVT.FTTECD.move(APIBH.getTECD());
            if (!APIBH.getSPYN().isBlank()) {
               PLCRTVT.FTSUNO.move(APIBH.getSPYN());
            } else {
               PLCRTVT.FTSUNO.move(APIBH.getSUNO());
            }
            PLCRTVT.FTCUNO.clear();
            PLCRTVT.FTCSCD.move(XXBSCD);
            PLCRTVT.FTECAR.move(XXECAR);
            if (XXEUVT == 1) {
               PLCRTVT.FTIOCD = 3;
            } else {
               PLCRTVT.FTIOCD = 2;
               sendFromToCountryAsInput();
            }
            PLCRTVT.FTAI11.clear();
            XVECAR.move(XXECAR);
            XVFTCO.move(XXFTCO);
            XVBSCD.move(XXBSCD);
            PLCRTVT.FTAI11.moveLeft(XVCFI1);
            PLCRTVT.CCRTVAT();
            if (!isBlank(PLCRTVT.FTVTM1, cRefVTAM.decimals())) {
               XCVTA1 = PLCRTVT.FTVTM1;
               XCVTA2 = PLCRTVT.FTVTM2;
               CR040.setVTP1(PLCRTVT.FTVTP1);
               CR040.setVTP2(PLCRTVT.FTVTP2);
               CR040.setVTAM(0d);
               XXVTAM = 0d;
               XXVTAM += PLCRTVT.FTVTM1;
               XXVTAM += PLCRTVT.FTVTM2;
               if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                   APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                   APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                   APIBH.getDIVI().NE(LDAZD.DIVI)) {
                  PLCLCCU.FZCUAM = XCVTA1;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.CCLCCUR();
                  XAVTA1 = PLCLCCU.FZACAM;
               } else {
                  XAVTA1 = XCVTA1;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = 0d;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZDCAM = 0;
                  PLCLCCU.FZLCDC = 0;
                  PLCLCCU.FZTX15.clear();
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZMSGA.clear();
                  IN92 = PLCLCCU.CCLCCUR();
               }
               if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                   APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                   APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                   APIBH.getDIVI().NE(LDAZD.DIVI)) {
                  PLCLCCU.FZCUAM = XCVTA2;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.CCLCCUR();
                  XAVTA2 = PLCLCCU.FZACAM;
               } else {
                  XAVTA2 = XCVTA2;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = 0d;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZDCAM = 0;
                  PLCLCCU.FZLCDC = 0;
                  PLCLCCU.FZTX15.clear();
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZMSGA.clear();
                  IN92 = PLCLCCU.CCLCCUR();
               }
               CR040.setARAT(PLCLCCU.FZARAT);
               CR040.setAIT1().move(PLCRTVT.FTAI11);
               CR040.setAIT2().move(PLCRTVT.FTAI12);
               CR040.setAIT3().move(PLCRTVT.FTAI13);
               CR040.setAIT4().move(PLCRTVT.FTAI14);
               CR040.setAIT5().move(PLCRTVT.FTAI15);
               CR040.setAIT6().move(PLCRTVT.FTAI16);
               CR040.setAIT7().move(PLCRTVT.FTAI17);
               ADAT04();
               IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
               if (IN91) {
                  CR040.setCUAM(XCVTA1);
                  CR040.setACAM(XAVTA1);
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     CR040.setVTAM(XXVTAM);
                  } else {
                     CR040.setVTAM(0d);
                  }
                  CR040.setDEDA(APIBH.getDEDA());
                  CR040.setARAT(PLCLCCU.FZARAT);
                  XXTRNO++;
                  CR040.setTRNO(XXTRNO);
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  //    Entry method
                  if (APIBH.getENME() == 1) {
                     // Scanned invoice
                     CR040.setENME(1);
                  } else {
                     CR040.setENME(0);
                  }
                  //   Accumulate for test of unbalanced voucher
                  XTACAM += CR040.getACAM();
                  XTCUAM += CR040.getCUAM();
                  CR040.setIVCL().moveLeftPad(invoiceClass); 	
                  CR040.WRITE("10");
               } else {
                  CR040.setCUAM(CR040.getCUAM() + XCVTA1);
                  CR040.setACAM(CR040.getACAM() + XAVTA1);
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     CR040.setVTAM(CR040.getVTAM() + XXVTAM);
                  } else {
                     CR040.setVTAM(0d);
                  }
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  //   Accumulate for test of unbalanced voucher
                  XTACAM += XAVTA1;
                  XTCUAM += XCVTA1;
                  CR040.UPDAT("10");
               }
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  XXVTAM = -(XXVTAM);
               }
               if (!isBlank(PLCRTVT.FTVTM2, cRefVTAM.decimals())) {
                  CR040.setAIT1().move(PLCRTVT.FTAI21);
                  CR040.setAIT2().move(PLCRTVT.FTAI22);
                  CR040.setAIT3().move(PLCRTVT.FTAI23);
                  CR040.setAIT4().move(PLCRTVT.FTAI24);
                  CR040.setAIT5().move(PLCRTVT.FTAI25);
                  CR040.setAIT6().move(PLCRTVT.FTAI26);
                  CR040.setAIT7().move(PLCRTVT.FTAI27);
                  ADAT04();
                  IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
                  if (IN91) {
                     CR040.setCUAM(XCVTA2);
                     CR040.setACAM(XAVTA2);
                     if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                        CR040.setVTAM(XXVTAM);
                     } else {
                        CR040.setVTAM(0d);
                     }
                     CR040.setDEDA(APIBH.getDEDA());
                     XXTRNO++;
                     CR040.setTRNO(XXTRNO);
                     CR040.setDBCR(' ');
                     if (CRS750DS.getPBDCNY() == 1) {
                        if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                           CR040.setDBCR(CRS750DS.getPBDBNG());
                        } else {
                           CR040.setDBCR(CRS750DS.getPBDBPS());
                        }
                     }
                     //    Entry method
                     //   Accumulate for test of unbalanced voucher
                     XTACAM += CR040.getACAM();
                     XTCUAM += CR040.getCUAM();
                     CR040.setIVCL().moveLeftPad(invoiceClass); 	
                     CR040.WRITE("10");
                  } else {
                     CR040.setCUAM(CR040.getCUAM() + XCVTA2);
                     CR040.setACAM(CR040.getACAM() + XAVTA2);
                     if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                        CR040.setVTAM(CR040.getVTAM() + XXVTAM);
                     } else {
                        CR040.setVTAM(0d);
                     }
                     CR040.setDBCR(' ');
                     if (CRS750DS.getPBDCNY() == 1) {
                        if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                           CR040.setDBCR(CRS750DS.getPBDBNG());
                        } else {
                           CR040.setDBCR(CRS750DS.getPBDBPS());
                        }
                     }
                     //   Accumulate for test of unbalanced voucher
                     XTACAM += XAVTA2;
                     XTCUAM += XCVTA2;
                     CR040.UPDAT("10");
                  }
               }
            }
         }
      }
      CR040.setVTP1(0d); 	
      CR040.setVTP2(0d); 	
   }

   /**
   * Update FCR040 with Currency deviation on VAT, if any
   */
   public void CURDEV() {
      XCDIFF = 0d;
      XCDIFF = (XAVTA1 + XAVTA2) - APIBL.getGLAM();
      if (!isBlank(XCDIFF, cRefACAM.decimals())) {
         XXTRNO++;
         CR040.setTRNO(XXTRNO);
         IN91 = !CR040.CHAIN_LOCK("00", CR040.getKey("00"));
         if (IN91) {
            CR040.setTRCD(41);
            CR040.setAPRV(XXAPRV);
            //    Set params for CRTVACC (VAT accounting)
            PXCRTVAC.PPCONO = APIBH.getCONO();
            PXCRTVAC.PPDIVI.move(CR040.getDIVI());
            PXCRTVAC.PPEVEN.move("AP50");
            if (greaterThan(XCDIFF, cRefACAM.decimals(), 0d)) {
               PXCRTVAC.PPACTY.move("302 ");
            } else {
               PXCRTVAC.PPACTY.move("301 ");
            }
            PXCRTVAC.PPFDAT = CR040.getACDT();
            PXCRTVAC.PPCMTP = LDAZD.CMTP;
            PXCRTVAC.PPLANC.move(LDAZD.LANC);
            PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
            if (!CR040.getSPYN().isBlank()) {
               PXCRTVAC.PPSUNO.move(CR040.getSPYN());
            } else {
               PXCRTVAC.PPSUNO.move(CR040.getSUNO());
            }
            PXCRTVAC.PPCUCD.move(CR040.getCUCD());
            XVBSCD.moveLeft(CR040.getBSCD());
            XVFTCO.moveLeft(CR040.getFTCO());
            XVECAR.moveLeft(XXECAR);
            PXCRTVAC.PPCFI1.moveLeft(XVCFI1);
            //    Create accounting references
            IN92 = PXCRTVAC.CRTVACC();
            CR040.setAIT1().move(PXCRTVAC.PPAIT1);
            CR040.setAIT2().move(PXCRTVAC.PPAIT2);
            CR040.setAIT3().move(PXCRTVAC.PPAIT3);
            CR040.setAIT4().move(PXCRTVAC.PPAIT4);
            CR040.setAIT5().move(PXCRTVAC.PPAIT5);
            CR040.setAIT6().move(PXCRTVAC.PPAIT6);
            CR040.setAIT7().move(PXCRTVAC.PPAIT7);
            CR040.setACAM(XCDIFF);
            CR040.setCUAM(0d);
            CR040.setVTCD(0);
            CR040.setVTAM(0d);
            CR040.setDBCR(' ');
            if (CRS750DS.getPBDCNY() == 1) {
               if (lessThan(CR040.getACAM(), cRefACAM.decimals(), 0d)) {
                  CR040.setDBCR(CRS750DS.getPBDBNG());
               } else {
                  CR040.setDBCR(CRS750DS.getPBDBPS());
               }
            }
            CR040.setDEDA(APIBH.getDEDA());
            ADAT04();
            CR040.setCVT1(0d);
            CR040.setCVT2(0d);
            XTACAM += CR040.getACAM();
            XTCUAM += CR040.getCUAM();
            CR040.setIVCL().moveLeftPad(invoiceClass);
            CR040.WRITE("00");
         } else {
            CR040.UNLOCK("00");
         }
      }
   }

   /**
   * Update FCR040 with Adjustments
   */
   public void ADJADD() {
      //Cleare some fields in FCR040
      CLR04();
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().move(APIBH.getDIVI());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.setRDTP(cRefRDTPext.ROUNDING_OFF());
      APIBL.SETLL("10", APIBL.getKey("10", 4));
      //   Read records
      IN93 = !APIBL.READE("10", APIBL.getKey("10", 4));
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      //   - Do for all adjustment records (=record type = 04)
      while (!IN93) {
         if (!isBlank(APIBL.getNLAM(), cRefNLAM.decimals())) {
            XXPYTO = APIBH.getCUAM();
            evaluateAmount = APIBL.getNLAM(); 	
            REPYTO();
            if (!isBlank(XXPYTO, cRefCUAM.decimals())) {
               CR040.setTRCD(41);
               CR040.setAPRV(XXAPRV);
               //    Set params for CRTVACC (Rounding off)
               PXCRTVAC.PPCONO = APIBH.getCONO();
               PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
               PXCRTVAC.PPEVEN.move(XXEVEN);
               PXCRTVAC.PPACTY.move("280 ");
               PXCRTVAC.PPFDAT = currentACDT;
               PXCRTVAC.PPCMTP = LDAZD.CMTP;
               PXCRTVAC.PPLANC.move(LDAZD.LANC);
               PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
               if (!APIBH.getSPYN().isBlank()) {
                  PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
               } else {
                  PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
               }
               //    Create accounting references
               IN92 = PXCRTVAC.CRTVACC();
               CR040.setAIT1().move(PXCRTVAC.PPAIT1);
               CR040.setAIT2().move(PXCRTVAC.PPAIT2);
               CR040.setAIT3().move(PXCRTVAC.PPAIT3);
               CR040.setAIT4().move(PXCRTVAC.PPAIT4);
               CR040.setAIT5().move(PXCRTVAC.PPAIT5);
               CR040.setAIT6().move(PXCRTVAC.PPAIT6);
               CR040.setAIT7().move(PXCRTVAC.PPAIT7);
               //   - Fetch amount in local currency
               if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                   APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                   APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                   APIBH.getDIVI().NE(LDAZD.DIVI)) {
                  PLCLCCU.FZCUAM = APIBL.getNLAM();
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.CCLCCUR();
                  XXACAM = PLCLCCU.FZACAM;
               } else {
                  XXACAM = APIBL.getNLAM();
                  PLCLCCU.FZARAT = APIBH.getARAT();
                  PLCLCCU.FZRAFA = 0;
                  PLCLCCU.FZCUAM = 0d;
                  PLCLCCU.FZACAM = 0d;
                  PLCLCCU.FZTEST = 0;
                  PLCLCCU.FZDCAM = 0;
                  PLCLCCU.FZLCDC = 0;
                  PLCLCCU.FZTX15.clear();
                  PLCLCCU.FZVERR = 0;
                  PLCLCCU.FZMSGI.clear();
                  PLCLCCU.FZMSGN = 0;
                  PLCLCCU.FZMSGA.clear();
                  IN92 = PLCLCCU.CCLCCUR();
               }
               XXCUAM = APIBL.getNLAM();
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTCD(APIBL.getVTCD());
               } else {
                  CR040.setVTCD(0);
               }
               XXVTAM = 0d;
               ADAT04();
               if (CR040.getAT04() >= 3 &&
                   CR040.getAT04() <= 9 &&
                   XXVTCD != 0) {
                  PLCRTVT.FTCONO = APIBH.getCONO();
                  PLCRTVT.FTDIVI.move(APIBH.getDIVI());
                  PLCRTVT.FTCMTP = LDAZD.CMTP;
                  PLCRTVT.FTTASK = 1;
                  PLCRTVT.FTVATH = 1;
                  PLCRTVT.FTVTCD = XXVTCD;
                  PLCRTVT.FTACDT = APIBH.getIVDT();
                  PLCRTVT.FTLCDC = X1LCDC;
                  PLCRTVT.FTBAAM = XXCUAM;
                  PLCRTVT.FTCUCD.move(APIBH.getCUCD());
                  PLCRTVT.FTEVEN.move(XXEVEN);
                  PLCRTVT.FTTECD.move(APIBH.getTECD());
                  if (!APIBH.getSPYN().isBlank()) {
                     PLCRTVT.FTSUNO.move(APIBH.getSPYN());
                  } else {
                     PLCRTVT.FTSUNO.move(APIBH.getSUNO());
                  }
                  PLCRTVT.FTCUNO.clear();
                  PLCRTVT.FTCSCD.move(XXBSCD);
                  PLCRTVT.FTECAR.move(XXECAR);
                  if (XXEUVT == 1) {
                     PLCRTVT.FTIOCD = 3;
                  } else {
                     PLCRTVT.FTIOCD = 2;
                     sendFromToCountryAsInput();
                  }
                  PLCRTVT.FTAI11.clear();
                  XVECAR.move(XXECAR);
                  XVFTCO.move(XXFTCO);
                  XVBSCD.move(XXBSCD);
                  PLCRTVT.FTAI11.moveLeft(XVCFI1);
                  PLCRTVT.CCRTVAT();
                  CR040.setVTCD(XXVTCD);
               } else {
                  PLCRTVT.FTVTM1 = 0D;
                  PLCRTVT.FTVTM2 = 0D;
               }
               IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
               if (IN91) {
                  XXTRNO++;
                  CR040.setTRNO(XXTRNO);
                  CR040.setCUAM(XXCUAM);
                  CR040.setACAM(XXACAM);
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     CR040.setVTAM(XXVTAM);
                     CR040.setCVT1(PLCRTVT.FTVTM1);
                     CR040.setCVT2(PLCRTVT.FTVTM2);
                  } else {
                     CR040.setVTAM(0d);
                  }
                  CR040.setDEDA(APIBH.getDEDA());
                  CR040.setARAT(PLCLCCU.FZARAT);
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  //    Entry method
                  if (APIBH.getENME() == 1) {
                     // Scanned invoice
                     CR040.setENME(1);
                  } else {
                     CR040.setENME(0);
                  }
                  //   Accumulate for test of unbalanced voucher
                  XTACAM += CR040.getACAM();
                  XTCUAM += CR040.getCUAM();
                  CR040.setIVCL().moveLeftPad(invoiceClass); 	
                  CR040.WRITE("10");
               } else {
                  CR040.setCUAM(CR040.getCUAM() + XXCUAM);
                  CR040.setACAM(CR040.getACAM() + XXACAM);
                  if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                     CR040.setVTAM(XXVTAM);
                     CR040.setCVT1(CR040.getCVT1() + PLCRTVT.FTVTM1);
                     CR040.setCVT2(CR040.getCVT2() + PLCRTVT.FTVTM2);
                  } else {
                     CR040.setVTAM(0d);
                  }
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  //   Accumulate for test of unbalanced voucher
                  XTACAM += XXACAM;
                  XTCUAM += XXCUAM;
                  CR040.UPDAT("10");
               }
            } else {
               X1CUAM = APIBL.getNLAM();
               X1ACAM = 0d;
               CLEADD();
            }
         }
         // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
         //   Read records
         IN93 = !APIBL.READE("10", APIBL.getKey("10", 4));
      }
   }

   /**
   * Invoice matching
   */
   public void INVMA() {
      // Fetch invoice matching user settings set from APS905DS
      // Read APS905 - AP - parameters
      found_CSYPAR_APS905 = cRefPGNMext.getCSYPAR_PGNM_DIVIorBlank(SYPAR, found_CSYPAR_APS905, LDAZD.CMTP, APIBH.getCONO(), APIBH.getDIVI(), "APS905");
      APS905DS.set().moveLeft(SYPAR.getPARM());
      APS36XDS.setAPS36XDS().clear();
      APS36XDS.setEFBJNO().move(GLS040_BJNO);
      APS36XDS.setEFCONO(APIBH.getCONO());
      APS36XDS.setEFCMTP(LDAZD.CMTP);
      APS36XDS.setEFDIVI().move(APIBH.getDIVI());
      APS36XDS.setEFSUNO().move(APIBH.getSUNO());
      APS36XDS.setEFSINO().move(APIBH.getSINO());
      APS36XDS.setEFINBN(APIBH.getINBN());
      APS36XDS.setEFINYR(CR040.getINYR());
      APS36XDS.setEFSPYN().move(CR040.getSPYN());
      APS36XDS.setEFACDT(currentACDT);
      APS36XDS.setEFAPCD().move(APIBH.getAPCD());
      APS36XDS.setEFTRNO(XXTRNO);
      APS36XDS.setEFBCHN(currentBCHN);
      APS36XDS.setEFLEFL(-(XTACAM));
      APS36XDS.setEFLEFC(-(XTCUAM));
      APS36XDS.setEFPUNO().move(APIBH.getPUNO());
      APS36XDS.setEFSERS(APIBH.getSERS());
      //
      IDADR.setCONO(APIBH.getCONO());
      IDADR.setSUNO().move(IDMAS.getSUNO());
      IDADR.setADTE(5);
      if (IDADR.CHAIN("00", IDADR.getKey("00", 3))) {
         IDMAS.setCSCD().move(IDADR.getCSCD());
         IDMAS.setECAR().move(IDADR.getECAR());
      }
      APS36XDS.setEFECAR().moveLeft(IDMAS.getECAR());
      //
      if (!APIBH.getFTCO().isBlank()) {
         APS36XDS.setEFCSCD().move(APIBH.getFTCO());
      } else {
         APS36XDS.setEFCSCD().move(IDMAS.getCSCD());
      }
      if (!APIBH.getBSCD().isBlank()) {
         APS36XDS.setEFBSCD().move(APIBH.getBSCD());
      } else {
         APS36XDS.setEFBSCD().move(MNDIV.getCSCD());
      }
      APS36XDS.setEFDEDA(APIBH.getDEDA());
      // Sales tax
      if (MNDIV.getTATM() == 2) {
         APS36XDS.setEFTXIN(APIBH.getTXIN());                
      }
      APMNGI03DS.clear();
      APMNGI03DS.setE5DUDT(APIBH.getDUDT());
      APMNGI03DS.setE5ACYP(CR040.getACYP());
      APMNGI03DS.setE5APCD().move(CR040.getAPCD());
      APMNGI03DS.setE5VSER().move(CR040.getVSER());
      APMNGI03DS.setE5FEID().move(CR040.getFEID());
      APMNGI03DS.setE5FNCN(CR040.getFNCN());
      APMNGI03DS.setE5TECD().move(CR040.getTECD());
      APMNGI03DS.setE5VTXT().move(CR040.getVTXT());
      APMNGI03DS.setE5VDSC().move(CR040.getVDSC());
      APMNGI03DS.setE5SUCL().move(CR040.getSUCL());
      APMNGI03DS.setE5VONO(CR040.getVONO());
      APMNGI03DS.setE5APRV(CR040.getAPRV());
      APMNGI03DS.setE5AVAT(XXCVAT);
      recordOK = true;
      if (APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING() ||
          APIBH.getPUNO().isBlank() ||
          APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE())) {
         //   -- Create invoice in FGINLI, FGINLC, FGINHC and FGINHE --
         APMNGI02DS.setAPMNGI02DS().clear();
         APMNGI02DS.setE4CUCD().move(CR040.getCUCD());
         APMNGI02DS.setE4CRTP(CR040.getCRTP());
         APMNGI02DS.setE4ARAT(CR040.getARAT());
         APMNGI02DS.setE4IVCU(APIBH.getCUAM());
         APMNGI02DS.setE4IVCU(APMNGI02DS.getE4IVCU() - APIBH.getVTAM());
         APMNGI02DS.setE4IVDT(APIBH.getIVDT());
         switch (0) {
            default:
               if (APIBH.getIBTP().EQ(cRefIBTPext.DEBIT_NOTE())) {
                  //   Debit note created from invoice matching
                  PXOPC.moveLeftPad("*AD7");
                  break;
               }
               PXOPC.moveLeftPad("*addBatch");
               break;
         }
         PXENV.move(toChar(false));
         //   Manage Invoice lines
         rMNGI02preCall();
         apCall("APMNGI02", rMNGI02);
         rMNGI02postCall();
         //   -- Check all invoice lines in FGINLI and FGINLC if not Debit note --
         if (APIBH.getIBTP().NE(cRefIBTPext.DEBIT_NOTE())) {
            APMNGI02DS.setAPMNGI02DS().clear();
            PXOPC.moveLeftPad("*CH3");
            PXENV.move(toChar(false));
            //   Manage Invoice lines
            rMNGI02preCall();
            apCall("APMNGI02", rMNGI02);
            rMNGI02postCall();
         }
      }
      if (APIBH.getIMCD() == cRefIMCDext.PO_HEAD_MATCHING()) {
         //Create invoice header
         APMNGI02DS.setAPMNGI02DS().clear();
         APMNGI02DS.setE4CUCD().move(CR040.getCUCD());
         APMNGI02DS.setE4CRTP(CR040.getCRTP());
         APMNGI02DS.setE4ARAT(CR040.getARAT());
         APMNGI02DS.setE4IVCU((APIBH.getCUAM()) - (APIBH.getVTAM()));
         APMNGI02DS.setE4IVDT(APIBH.getIVDT());
         PXOPC.moveLeftPad("*AD6");
         PXENV.move(toChar(false));
         //Manage invoice Lines
         rMNGI02preCall();
         apCall("APMNGI02", rMNGI02);
         rMNGI02postCall();
         //Select all receipt for this PO and update file FGI350
         APMNGI01DS.setAPMNGI01DS().clear();
         APMNGI01DS.setE3CUCD().move(CR040.getCUCD());
         PXOPC.moveLeftPad("*AD2");
         PXENV.move(toChar(false));
         //Manage file FGI350
         rMNGI01preCall();
         apCall("APMNGI01", rMNGI01);
         rMNGI01postCall();
         switch (0) { 	
            default: 	
               if (PXMSID.equals("AP35003")) {
                  // No goods receipt found 	
                  if (APS905DS.getYXIM20() == 1 || IDVEN.getIAPC() == 0) {
                     // Do not select all lines for this PO
                     recordOK = false;
                  } else {
                     // Select all lines for this PO
                     APMNGI01DS.setAPMNGI01DS().clear();
                     PXOPC.moveLeftPad("*AD5");
                     PXENV.move(toChar(false));
                     //Manage file FGI350
                     rMNGI01preCall();
                     apCall("APMNGI01", rMNGI01);
                     rMNGI01postCall();
                     // No lines found
                     if (!PXMSID.equals("AP35001")) {
                        recordOK = false;
                     }
                  }
                  break; 	
               } 	
               if (PXMSID.equals("AP35005")) { 	
                  // Goods receipt found but not completed 	
                  if (APS905DS.getYXIM50() == 1) {
                     // --Delete records in FGI350 --
                     APMNGI01DS.setAPMNGI01DS().clear();
                     APMNGI01DS.setE3CUCD().move(CR040.getCUCD());
                     PXOPC.moveLeftPad("*DL1");
                     PXENV.move(toChar(false));
                     // Manage file -  FGI350
                     rMNGI01preCall();
                     apCall("APMNGI01", rMNGI01);
                     rMNGI01postCall();
                     recordOK = false;
                  } else if ((APS905DS.getYXIM50() == 3 || 	
                             APS905DS.getYXIM50() == 4) && 	
                             IDVEN.getIAPC() != 0) { 	
                     APMNGI01DS.setAPMNGI01DS().clear();
                     PXOPC.moveLeftPad("*AD7");
                     PXENV.move(toChar(false));
                     //Manage file FGI350
                     rMNGI01preCall();
                     apCall("APMNGI01", rMNGI01);
                     rMNGI01postCall();
                  }
                  break; 	
               }
               break; 	
         }
         if (recordOK) {
         // Create invoice lines for the selected lines
            APMNGI02DS.setAPMNGI02DS().clear();
            PXOPC.moveLeftPad("*AD1");
            PXENV.move(toChar(false));
            // Manage Invoice Lines
            rMNGI02preCall();
            apCall("APMNGI02", rMNGI02);
            rMNGI02postCall();
            // --Delete records in FGI350 --
            APMNGI01DS.setAPMNGI01DS().clear();
            APMNGI01DS.setE3CUCD().move(CR040.getCUCD());
            PXOPC.moveLeftPad("*DL1");
            PXENV.move(toChar(false));
            // Manage file -  FGI350
            rMNGI01preCall();
            apCall("APMNGI01", rMNGI01);
            rMNGI01postCall();
            // Create invoice header charges based on FAPIBL
            PXOPC.moveLeftPad("*AD8");
            PXENV.move(toChar(false));
            // Manage invoice lines
            rMNGI02preCall();
            apCall("APMNGI02", rMNGI02);
            rMNGI02postCall();
            // set accumulator values
            setAccumulatorValues();
            // Get total allowed variance from APMNGI02
            APMNGI02DS.setAPMNGI02DS().clear();
            PXOPC.moveLeftPad("*GTV");
            PXENV.move(toChar(false));
            // Manage approve one line FGINLI
            rMNGI02preCall();
            apCall("APMNGI02", rMNGI02);
            rMNGI02postCall();
            //Calculate left to invoice - amount
            XSLASK = APIBH.getCUAM();
            XSLASK -= APIBH.getVTAM();
            XSLASK -= X5IVNA;
            // Change to positive amount
            XSLAS1 = XSLASK;
            if (lessThan(XSLAS1, cRefIVNA.decimals(), 0d)) { 	
               XSLAS1 = (-XSLAS1);
            }
            // calulate left to invoice - percentage
            if (!isBlank(X5IVNA, cRefIVNA.decimals())) { 	
               XSLAS2 = (XSLAS1/X5IVNA);
            } else {
               XSLAS2 = 0;
            }
            XSLAS2 = (XSLAS2 * 100);
            if (useTaxFound()) {
               // if use tax found, record is not ok, recoding needed
               recordOK = false;
            } else {
               //Whitin limits
               if (greaterThan(XSLAS1, cRefIVNA.decimals(), APS36XDS.getEFDEAI()) || 	
                   greaterThan(XSLAS2, cRefIVNA.decimals(), APS36XDS.getEFPDEI())) { 	
                  recordOK = false;
               } else {
                  if (!isBlank(XSLASK, cRefIVNA.decimals())) { 	
                     //Distribute the variance amount to the invoice lines
                     if (greaterOrEquals(XSLAS1, cRefIVNA.decimals(), APS36XDS.getEFQEAI())) { 	
                        // set parameters for CALL of APMNGI02
                        APMNGI02DS.setAPMNGI02DS().clear();
                        APMNGI02DS.setE4OPE1().move(02);
                        APMNGI02DS.setE4IVNA(XSLASK);
                        PXOPC.moveLeftPad("*DI2");
                        PXENV.move(toChar(false));
                        // Manage distribute variance to invoice lines to inv net amount
                        rMNGI02preCall();
                        apCall("APMNGI02", rMNGI02);
                        rMNGI02postCall();
                        // Set accumulator values again
                        setAccumulatorValues();
                        // Calculate left to invoice -Amount
                        XSLASK = APIBH.getCUAM();
                        XSLASK -= APIBH.getVTAM();
                        XSLASK -= X5IVNA;
                     }
                     if (!isBlank(XSLASK, cRefCUAM.decimals())) { 	
                        // -- create PP20-240 record in FGINAE for amount left to invoice --
                        APMNGI03DS.setE5CUAM(XSLASK);
                        PXOPC.moveLeftPad("*AD3");
                        PXENV.move(toChar(false));
                        // Manage - create account entries
                        rMNGI03preCall();
                        apCall("APMNGI03", rMNGI03);
                        rMNGI03postCall();
                     }
                  }
                  // -- approve all invoice lines --
                  APMNGI02DS.setAPMNGI02DS().clear();
                  PXOPC.moveLeftPad("*AI1");
                  PXENV.move(toChar(false));
                  // Manage invoice lines FGINLI
                  rMNGI02preCall();
                  apCall("APMNGI02", rMNGI02);
                  rMNGI02postCall();
               }
            }
         } else {
            // record not ok. Delete invoice header
            APMNGI04DS.setAPMNGI04DS().clear();
            PXOPC.moveLeftPad("*DL1");
            PXENV.move(toChar(false));
            rMNGI04preCall();
            apCall("APMNGI04", rMNGI04);
            rMNGI04postCall();
         }
      }//End if E1IMCD
      if (recordOK) {
         //   -- Create acc entries for all appr lines in FGINAE --
         PXOPC.moveLeftPad("*AD1");
         PXENV.move(toChar(false));
         //   Manage - Create account entries
         rMNGI03preCall();
         apCall("APMNGI03", rMNGI03);
         rMNGI03postCall();
         //   -- Update FCR040 with acc entries from FGINAE --
         APS36XDS.setEFLEFL(-(XTACAM));
         APS36XDS.setEFLEFC(-(XTCUAM));
         if (APS36XDS.getEFDMSG() == '1' && PXMSID.equals("AP36022")) {
         } else {
            PXOPC.moveLeftPad("*VO1");
            PXENV.move(toChar(false));
            //   Manage - Create account entries
            rMNGI03preCall();
            apCall("APMNGI03", rMNGI03);
            rMNGI03postCall();
            //   Update Left to distribute
            XTACAM = -(APS36XDS.getEFLEFL());
            XTCUAM = -(APS36XDS.getEFLEFC());
            XXTRNO = APS36XDS.getEFTRNO();
            currentBCHN = APS36XDS.getEFBCHN();
         }
      }
      if (APS36XDS.getEFDMSG() == '1' && PXMSID.equals("AP36022")) {
      } else {
         // Update FGRECL and FGRPCL
         APMNGI04DS.setAPMNGI04DS().clear();
         PXOPC.moveLeftPad("*UP4");
         PXENV.move(toChar(false));
         rMNGI04preCall();
         apCall("APMNGI04", rMNGI04);
         rMNGI04postCall();
      }
      //   Post left to distribute to clearing account
      if (!isBlank(APS36XDS.getEFLEFC(), cRefCUAM.decimals())) {
         XXPYTO = APIBH.getCUAM(); 	 	
         if (lessThan(APS36XDS.getEFLEFC(), cRefCUAM.decimals(), 0d)) {  	
            evaluateAmount = -(APS36XDS.getEFLEFC());  	
         } else { 	 	
            evaluateAmount = APS36XDS.getEFLEFC();  	
         } 	 	
         REPYTO();  	
         if (!isBlank(XXPYTO, cRefCUAM.decimals())) {  	
            X2CUAM = APS36XDS.getEFLEFC();  	
            X2ACAM = 0d; 	 	
            accountLeftToDistrubute();  	
         } else { 	
            X1CUAM = APS36XDS.getEFLEFC();
            X1ACAM = 0d;
            //   Auto VAT 4 = Clearing account splitted per VAT code 	 	 	 	
            if (automaticVATaccounting_AVAT != 4) { 	 	 	 	 	
               CLEADD();
               if (XXCVAT != 0 && !computeVAT) { 	 	
                  writeVATRecord();     	
               }
            } else { 	
               splitClearAccByVTCD(); 	 	 	 	
            } 	 	
         }             	
      } else { 	
         //   PRDE = 2 - Approved depending on status from invoice matching
         if (notApprovedForPayment_PRDE == 2) {
            //   -- Chain FGINHE --
            GINHE.setCONO(APIBH.getCONO());
            GINHE.setDIVI().move(APIBH.getDIVI());
            GINHE.setSUNO().move(APIBH.getSUNO());
            GINHE.setSINO().move(APIBH.getSINO());
            GINHE.setINYR(CR040.getINYR());
            IN91 = !GINHE.CHAIN("00", GINHE.getKey("00"));
            //   If invoice matching fully approved change to approved for payment
            if (!IN91) {
               DSINS0.move(GINHE.getINS0());
               if (DSINS1.getChar() == '3' &&
                   DSINS2.getChar() == '3' &&
                   DSINS3.getChar() == '3' &&
                   DSINS4.getChar() == '3' &&
                   DSINS5.getChar() == '3') {
                  XXAPRV = 1;
                  //   -- Read first record in FCR040 --
                  CR040.setSTCF(0);
                  CR040.setBCHN(currentBCHN);
                  CR040.SETLL("00", CR040.getKey("00", 5));
                  IN93 = !CR040.READE_LOCK("00", CR040.getKey("00", 5));
                  while (!IN93) {
                     CR040.setAPRV(XXAPRV);
                     CR040.UPDAT("00");
                     //   -- Read next record in FCR040 --
                     IN93 = !CR040.READE_LOCK("00", CR040.getKey("00", 5));
                  }
               }
            }
         }
      }
   }

   /**
   * Update FCR040 with Charges
   */
   public void CHGADD() {
      //Cleare some fields in FCR040
      CLR04();
      APIBL.setCONO(APIBH.getCONO());
      APIBL.setDIVI().move(APIBH.getDIVI());
      APIBL.setINBN(APIBH.getINBN());
      APIBL.setRDTP(cRefRDTPext.ORDER_CHARGES());
      APIBL.SETLL("10", APIBL.getKey("10", 4));
      //   Read records
      IN93 = !APIBL.READE("10", APIBL.getKey("10", 4));
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      //   - Do for all charge records (=record type = 02)
      while (!IN93) {
         if (!isBlank(APIBL.getNLAM(), cRefNLAM.decimals())) {
            CR040.setTRCD(41);
            CR040.setAPRV(XXAPRV);
            //    Set params for CRTVACC (VAT accounting)
            PXCRTVAC.PPCONO = APIBH.getCONO();
            PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
            PXCRTVAC.PPEVEN.move(XXEVEN);
            PXCRTVAC.PPACTY.move("230 ");
            PXCRTVAC.PPFDAT = currentACDT;
            PXCRTVAC.PPCMTP = LDAZD.CMTP;
            PXCRTVAC.PPLANC.move(LDAZD.LANC);
            PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
            if (!APIBH.getSPYN().isBlank()) {
               PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
            } else {
               PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
            }
            if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
               if (APIBL.getVTCD() == 0 && !APIBL.getCEID().isBlank()) {
                  found_MPCELE = cRefCEIDext.getMPCELE(PCELE, found_MPCELE, APIBH.getCONO(), APIBL.getCEID());
                  if (found_MPCELE) {
                     APIBL.setVTCD(PCELE.getVTCD());
                  }
               }
               PXCRTVAC.PPVTCD = APIBL.getVTCD();
            } else {
               PXCRTVAC.PPVTCD = 0;
            }
            PXCRTVAC.PPCEID.move(APIBL.getCEID());
            //    Create accounting references
            IN92 = PXCRTVAC.CRTVACC();
            CR040.setAIT1().move(PXCRTVAC.PPAIT1);
            CR040.setAIT2().move(PXCRTVAC.PPAIT2);
            CR040.setAIT3().move(PXCRTVAC.PPAIT3);
            CR040.setAIT4().move(PXCRTVAC.PPAIT4);
            CR040.setAIT5().move(PXCRTVAC.PPAIT5);
            CR040.setAIT6().move(PXCRTVAC.PPAIT6);
            CR040.setAIT7().move(PXCRTVAC.PPAIT7);
            //   - Fetch amount in local currency
            if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                APIBH.getDIVI().NE(LDAZD.DIVI)) {
               PLCLCCU.FZCUAM = APIBL.getNLAM();
               PLCLCCU.FZACAM = 0d;
               PLCLCCU.FZARAT = APIBH.getARAT();
               PLCLCCU.CCLCCUR();
               XXACAM = PLCLCCU.FZACAM;
            } else {
               XXACAM = APIBL.getNLAM();
               PLCLCCU.FZARAT = APIBH.getARAT();
               PLCLCCU.FZRAFA = 0;
               PLCLCCU.FZCUAM = 0d;
               PLCLCCU.FZACAM = 0d;
               PLCLCCU.FZTEST = 0;
               PLCLCCU.FZDCAM = 0;
               PLCLCCU.FZLCDC = 0;
               PLCLCCU.FZTX15.clear();
               PLCLCCU.FZVERR = 0;
               PLCLCCU.FZMSGI.clear();
               PLCLCCU.FZMSGN = 0;
               PLCLCCU.FZMSGA.clear();
               IN92 = PLCLCCU.CCLCCUR();
            }
            XXCUAM = APIBL.getNLAM();
            if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
               CR040.setVTCD(APIBL.getVTCD());
            } else {
               CR040.setVTCD(0);
            }
            XXVTAM = 0d;
            ADAT04();
            if (CR040.getAT04() >= 3 &&
                CR040.getAT04() <= 9 &&
                CR040.getVTCD() != 0) {
               PLCRTVT.FTCONO = APIBH.getCONO();
               PLCRTVT.FTDIVI.move(APIBH.getDIVI());
               PLCRTVT.FTCMTP = LDAZD.CMTP;
               PLCRTVT.FTTASK = 1;
               PLCRTVT.FTVATH = 1;
               PLCRTVT.FTVTCD = CR040.getVTCD();
               PLCRTVT.FTACDT = APIBH.getIVDT();
               PLCRTVT.FTLCDC = X1LCDC;
               PLCRTVT.FTBAAM = XXCUAM;
               PLCRTVT.FTCUCD.move(APIBH.getCUCD());
               PLCRTVT.FTEVEN.move(XXEVEN);
               PLCRTVT.FTTECD.move(APIBH.getTECD());
               if (!APIBH.getSPYN().isBlank()) {
                  PLCRTVT.FTSUNO.move(APIBH.getSPYN());
               } else {
                  PLCRTVT.FTSUNO.move(APIBH.getSUNO());
               }
               PLCRTVT.FTCUNO.clear();
               PLCRTVT.FTCSCD.move(XXBSCD);
               PLCRTVT.FTECAR.move(XXECAR);
               if (XXEUVT == 1) {
                  PLCRTVT.FTIOCD = 3;
               } else {
                  PLCRTVT.FTIOCD = 2;
                  sendFromToCountryAsInput();
               }
               PLCRTVT.FTAI11.clear();
               XVECAR.move(XXECAR);
               XVFTCO.move(XXFTCO);
               XVBSCD.move(XXBSCD);
               PLCRTVT.FTAI11.moveLeft(XVCFI1);
               PLCRTVT.CCRTVAT();
            } else {
               PLCRTVT.FTVTM1 = 0D;
               PLCRTVT.FTVTM2 = 0D;
            }
            IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
            if (IN91) {
               XXTRNO++;
               CR040.setTRNO(XXTRNO);
               CR040.setCUAM(XXCUAM);
               CR040.setACAM(XXACAM);
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTAM(XXVTAM);
                  CR040.setCVT1(PLCRTVT.FTVTM1);
                  CR040.setCVT2(PLCRTVT.FTVTM2);
               } else {
                  CR040.setVTAM(0d);
               }
               CR040.setDEDA(APIBH.getDEDA());
               CR040.setARAT(PLCLCCU.FZARAT);
               CR040.setDBCR(' ');
               if (CRS750DS.getPBDCNY() == 1) {
                  if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                     CR040.setDBCR(CRS750DS.getPBDBNG());
                  } else {
                     CR040.setDBCR(CRS750DS.getPBDBPS());
                  }
               }
               //    Entry method
               if (APIBH.getENME() == 1) {
                  // Scanned invoice
                  CR040.setENME(1);
               } else {
                  CR040.setENME(0);

               }
               //   Accumulate for test of unbalanced voucher
               XTACAM += CR040.getACAM();
               XTCUAM += CR040.getCUAM();
               CR040.setIVCL().moveLeftPad(invoiceClass); 	
               CR040.WRITE("10");
            } else {
               CR040.setCUAM(CR040.getCUAM() + XXCUAM);
               CR040.setACAM(CR040.getACAM() + XXACAM);
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTAM(XXVTAM);
                  CR040.setCVT1(CR040.getCVT1() + PLCRTVT.FTVTM1);
                  CR040.setCVT2(CR040.getCVT2() + PLCRTVT.FTVTM2);
               } else {
                  CR040.setVTAM(0d);
               }
               CR040.setDBCR(' ');
               if (CRS750DS.getPBDCNY() == 1) {
                  if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                     CR040.setDBCR(CRS750DS.getPBDBNG());
                  } else {
                     CR040.setDBCR(CRS750DS.getPBDBPS());
                  }
               }
               //   Accumulate for test of unbalanced voucher
               XTACAM += XXACAM;
               XTCUAM += XXCUAM;
               CR040.UPDAT("10");
            }
         }
         // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
         //   Read records
         IN93 = !APIBL.READE("10", APIBL.getKey("10", 4));
      }
   }

   /**
   * Update FCR040 with clearing account transaction
   */
   public void CLEADD() {
      //
      if (XXCVAT == 1 && XXEUVT == 1) {
         comingFromCLEADD = true; 	
         VATADD();
         // Line type 3 overrule EU-vat. CUAM need to be adjusted for VAT accounting
         X1CUAM = -(XTCUAM);
         X1ACAM = 0d;
      }
      //   If the amount is already zero, no need to create a clearing account
      if (isBlank(XTCUAM, cRefCUAM.decimals()) &&
          isBlank(XTACAM, cRefACAM.decimals())) {
         return;
      }
      CR040.setTRCD(41);
      CR040.setAPRV(XXAPRV);
      //Cleare some fields in FCR040
      CLR04();
      CR040.setAIT1().clear();
      CR040.setAIT2().clear();
      CR040.setAIT3().clear();
      CR040.setAIT4().clear();
      CR040.setAIT5().clear();
      CR040.setAIT6().clear();
      CR040.setAIT7().clear();
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      //    Set params for CRTVACC (Entry of inv-Clear account supp inv)
      PXCRTVAC.PPCONO = APIBH.getCONO();
      PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
      PXCRTVAC.PPEVEN.move(XXEVEN);
      PXCRTVAC.PPACTY.move("215 ");
      PXCRTVAC.PPFDAT = currentACDT;
      PXCRTVAC.PPCMTP = LDAZD.CMTP;
      PXCRTVAC.PPLANC.move(LDAZD.LANC);
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
      PXCRTVAC.PPVTCD = XXVTCD; 	
      XVBSCD.moveLeftPad(XXBSCD); 	
      XVFTCO.moveLeftPad(XXFTCO); 	
      XVECAR.moveLeftPad(XXECAR); 	
      PXCRTVAC.PPCFI1.moveLeftPad(XVCFI1);
      if (!APIBH.getSPYN().isBlank()) {
         PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
      } else {
         PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
      }
      //    Create accounting references
      IN92 = PXCRTVAC.CRTVACC();
      CR040.setAIT1().move(PXCRTVAC.PPAIT1);
      CR040.setAIT2().move(PXCRTVAC.PPAIT2);
      CR040.setAIT3().move(PXCRTVAC.PPAIT3);
      CR040.setAIT4().move(PXCRTVAC.PPAIT4);
      CR040.setAIT5().move(PXCRTVAC.PPAIT5);
      CR040.setAIT6().move(PXCRTVAC.PPAIT6);
      CR040.setAIT7().move(PXCRTVAC.PPAIT7);
      if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
          APIBH.getDIVI().EQ(LDAZD.DIVI) ||
          APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
          APIBH.getDIVI().NE(LDAZD.DIVI)) {
         PLCLCCU.FZCUAM = X1CUAM;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZARAT = APIBH.getARAT();
         IN92 = PLCLCCU.CCLCCUR();
         XXACAM = PLCLCCU.FZACAM;
      } else {
         XXACAM = X1CUAM;
         PLCLCCU.FZARAT = APIBH.getARAT();
         PLCLCCU.FZRAFA = 0;
         PLCLCCU.FZCUAM = 0d;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZTEST = 0;
         PLCLCCU.FZDCAM = 0;
         PLCLCCU.FZLCDC = 0;
         PLCLCCU.FZTX15.clear();
         PLCLCCU.FZVERR = 0;
         PLCLCCU.FZMSGI.clear();
         PLCLCCU.FZMSGN = 0;
         PLCLCCU.FZMSGA.clear();
         IN92 = PLCLCCU.CCLCCUR();
      }
      XXCUAM = X1CUAM;
      XXVTAM = 0d;
      //   Invoice matching
      if (APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) {
         CR040.setPINC(4);
      } else {
         CR040.setPINC(0);
      }
      ADAT04();
      if (CR040.getAT04() >= 3 && CR040.getAT04() <= 9 && XXVTCD != 0) {
         PLCRTVT.FTCONO = APIBH.getCONO();
         PLCRTVT.FTDIVI.move(APIBH.getDIVI());
         PLCRTVT.FTCMTP = LDAZD.CMTP;
         PLCRTVT.FTTASK = 3;
         PLCRTVT.FTEVEN.move("AP50");
         PLCRTVT.FTVATH = 1;
         PLCRTVT.FTVTCD = XXVTCD;
         PLCRTVT.FTACDT = APIBH.getIVDT();
         PLCRTVT.FTLCDC = X1LCDC;
         PLCRTVT.FTBAAM = XXCUAM;         
         PLCRTVT.FTCUCD.move(APIBH.getCUCD());
         PLCRTVT.FTEVEN.move(XXEVEN);
         PLCRTVT.FTTECD.move(APIBH.getTECD());
         if (!APIBH.getSPYN().isBlank()) {
            PLCRTVT.FTSUNO.move(APIBH.getSPYN());
         } else {
            PLCRTVT.FTSUNO.move(APIBH.getSUNO());
         }
         PLCRTVT.FTCUNO.clear();
         PLCRTVT.FTCSCD.move(XXBSCD);
         PLCRTVT.FTECAR.move(XXECAR);
         if (XXEUVT == 1) {
            PLCRTVT.FTIOCD = 3;
         } else {
            PLCRTVT.FTIOCD = 2;
            sendFromToCountryAsInput();
         }
         PLCRTVT.FTAI11.clear();
         XVECAR.move(XXECAR);
         XVFTCO.move(XXFTCO);
         XVBSCD.move(XXBSCD);
         PLCRTVT.FTAI11.moveLeft(XVCFI1);
         PLCRTVT.CCRTVAT();
      } else {
         PLCRTVT.FTVTM1 = 0D;
         PLCRTVT.FTVTM2 = 0D;
      }
      XXSUNO.move(CR040.getSUNO());
      XXSINO.moveLeft(CR040.getSINO());
      XXINYR.move(CR040.getINYR());
      XXALSU.moveLeft(IDMAS.getALSU());
      CR040.setVTXT().moveLeft(XXVTXT);
      // Set VAT code if no existing VAT line
      if (!hasVATLine) {
         XXVTCD = CR040.getVTCD();
      }
      CR040.setVTCD(XXVTCD);
      IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
      if (IN91) {
         if (XXCVAT != 0) { 	
            XXCUAM -= PLCRTVT.FTVTM1; 	
            XXCUAM -= PLCRTVT.FTVTM2; 	
            if (APIBH.getCUCD().NE(LDAZD.LOCD) && APIBH.getDIVI().EQ(LDAZD.DIVI) || 	
                APIBH.getCUCD().NE(MNDIV.getLOCD()) && APIBH.getDIVI().NE(LDAZD.DIVI)) { 	
               PLCLCCU.FZCUAM = XXCUAM; 	
               PLCLCCU.FZACAM = 0d; 	
               PLCLCCU.FZARAT = APIBH.getARAT(); 	
               PLCLCCU.CCLCCUR(); 	
               XXACAM = PLCLCCU.FZACAM; 	
            } else { 	
               XXACAM = XXCUAM; 	
            } 	
         }
         XXTRNO++;
         CR040.setTRNO(XXTRNO);
         CR040.setCUAM(XXCUAM);
         CR040.setACAM(XXACAM);
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setVTAM(XXVTAM);
            CR040.setCVT1(PLCRTVT.FTVTM1);
            CR040.setCVT2(PLCRTVT.FTVTM2);
            CR040.setVTP1(PLCRTVT.FTVTP1); 	 	 	
            CR040.setVTP2(PLCRTVT.FTVTP2); 	
         } else {
            CR040.setVTAM(0d);
         }
         CR040.setDEDA(APIBH.getDEDA());
         CR040.setARAT(PLCLCCU.FZARAT);
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            }
         }
         //    Entry method
         if (APIBH.getENME() == 1) {
            // Scanned invoice
            CR040.setENME(1);
         } else {
            CR040.setENME(0);
         }
         //   Accumulate for test of unbalanced voucher
         XTACAM += CR040.getACAM();
         XTCUAM += CR040.getCUAM();
         CR040.setIVCL().moveLeftPad(invoiceClass); 	
         CR040.WRITE("10");
         // Check for deductible VAT
         getVATRate(CR040.getVTCD());
         if (!isBlank(VATPC.getVTD1(), EPS_2)) {
            XXCVT1 = CR040.getCVT1();
            XXCVT2 = CR040.getCVT2();
            if (!isBlank(XXCVT1, EPS_2)) {
               // Save accounting dimensions 1 - 7       
               // from Cost base record (AT04 >= 3)      
               savedAIT1_Cost.move(CR040.getACA()[0]);      
               savedAIT2_Cost.move(CR040.getACA()[1]);      
               savedAIT3_Cost.move(CR040.getACA()[2]);     
               savedAIT4_Cost.move(CR040.getACA()[3]);      
               savedAIT5_Cost.move(CR040.getACA()[4]);      
               savedAIT6_Cost.move(CR040.getACA()[5]);      
               savedAIT7_Cost.move(CR040.getACA()[6]);   
               // Save accounting dimensions 1 - 7    
               // fo VAT accounting 1 (AT04 <= 2)     
               savedAIT1_VAT1.move(PLCRTVT.FTAI11);      
               savedAIT2_VAT1.move(PLCRTVT.FTAI12);      
               savedAIT3_VAT1.move(PLCRTVT.FTAI13);      
               savedAIT4_VAT1.move(PLCRTVT.FTAI14);      
               savedAIT5_VAT1.move(PLCRTVT.FTAI15);      
               savedAIT6_VAT1.move(PLCRTVT.FTAI16);      
               savedAIT7_VAT1.move(PLCRTVT.FTAI17);
               calculateDeductable(XXCVT1, VATPC.getVTD1());
               createDeductibleVAT_1();
            }
            // If combined VAT
            if (!isBlank(XXCVT2, EPS_2) &&
                PLCHKIF.FTEUVT != 1) {
               // Save accounting dimensions 1 - 7
               // fo VAT accounting 2 (AT04 <= 2))
               savedAIT1_VAT2.move(PLCRTVT.FTAI21);
               savedAIT2_VAT2.move(PLCRTVT.FTAI22);
               savedAIT3_VAT2.move(PLCRTVT.FTAI23);
               savedAIT4_VAT2.move(PLCRTVT.FTAI24);
               savedAIT5_VAT2.move(PLCRTVT.FTAI25);
               savedAIT6_VAT2.move(PLCRTVT.FTAI26);
               savedAIT7_VAT2.move(PLCRTVT.FTAI27);
               calculateDeductable(XXCVT2, VATPC.getVTD1());
               createDeductibleVAT_2();
            }
         }
         // Self assessed Tax
         if (PLCRTVT.FTIOCD == 2 && DSVTCD.getYKSATX() == 1 &&
             DSVTCD.getYKVATT() == 2) {
            if (!isBlank(PLCRTVT.FTVTM3, EPS_2)) {
               createSelfAssessedTax();
            }
         }
      } else {
         CR040.setCUAM(CR040.getCUAM() + XXCUAM);
         CR040.setACAM(CR040.getACAM() + XXACAM);
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setVTAM(XXVTAM);
            CR040.setCVT1(CR040.getCVT1() + PLCRTVT.FTVTM1);
            CR040.setCVT2(CR040.getCVT2() + PLCRTVT.FTVTM2);
         } else {
            CR040.setVTAM(0d);
         }
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            }
         }
         //   Accumulate for test of unbalanced voucher
         XTACAM += XXACAM;
         XTCUAM += XXCUAM;
         CR040.UPDAT("10");
      }
      CR040.setVTXT().move(APS455DS.getZWVTXT());
   }

   /**
   * Read and calculate the clearing amount for each VAT code
   */
   public void splitClearAccByVTCD() { 	 	 	 	
      // Read FCR040 and MPLINE for this invoice and save in arrays 	 	
      // VTP1 = VAT rates, RVAT = Recorded VAT amount, GAMT = Goods net amount
      // VTD1 = Deductable VAT rates
      fillVATArrays(); 	 	 	 	
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 	 	 	
      //    Set params for CRTVACC (Entry of inv-Clear account supp inv) 	 	 	
      PXCRTVAC.PPCONO = APIBH.getCONO(); 	 	 	
      PXCRTVAC.PPDIVI.move(APIBH.getDIVI()); 	 	 	
      PXCRTVAC.PPEVEN.move(XXEVEN); 	 	 	
      PXCRTVAC.PPACTY.move("215 "); 	 	 	
      PXCRTVAC.PPFDAT = currentACDT; 	 	 	
      PXCRTVAC.PPCMTP = LDAZD.CMTP; 	 	 	
      PXCRTVAC.PPLANC.move(LDAZD.LANC); 	 	 	
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM); 	 	 	
      if (!APIBH.getSPYN().isBlank()) { 	 	 	
         PXCRTVAC.PPSUNO.move(APIBH.getSPYN()); 	 	 	
      } else { 	 	 	
         PXCRTVAC.PPSUNO.move(APIBH.getSUNO()); 	 	 	
      } 	 	 	
      //    Create accounting references 	 	 	
      IN92 = PXCRTVAC.CRTVACC(); 	 	 	
      CR040.setAIT1().moveLeft(PXCRTVAC.PPAIT1); 	 	 	
      CR040.setAIT2().moveLeft(PXCRTVAC.PPAIT2); 	 	 	
      CR040.setAIT3().moveLeft(PXCRTVAC.PPAIT3); 	 	 	
      CR040.setAIT4().moveLeft(PXCRTVAC.PPAIT4); 	 	 	
      CR040.setAIT5().moveLeft(PXCRTVAC.PPAIT5); 	 	 	
      CR040.setAIT6().moveLeft(PXCRTVAC.PPAIT6); 	 	 	
      CR040.setAIT7().moveLeft(PXCRTVAC.PPAIT7);
      //  Save accounting dimensions for clearing account 	
      XXAIT1.moveLeftPad(PXCRTVAC.PPAIT1); 	
      XXAIT2.moveLeftPad(PXCRTVAC.PPAIT2); 	
      XXAIT3.moveLeftPad(PXCRTVAC.PPAIT3); 	
      XXAIT4.moveLeftPad(PXCRTVAC.PPAIT4); 	
      XXAIT5.moveLeftPad(PXCRTVAC.PPAIT5); 	
      XXAIT6.moveLeftPad(PXCRTVAC.PPAIT6); 	
      XXAIT7.moveLeftPad(PXCRTVAC.PPAIT7); 	

      noVATClearAcc = false;
      unbalancedVoucher = false;
      IX = 0; 	 	 	
      while (IX < 99) { 	 	 	
         IX++; 	 	 	
         if (!isBlank(RVAT[IX - 1], cRefVTAM.decimals())) {	 	 	 	
            XPVTP1 = VTP1[IX - 1] / 100d; 
            if (equals(XPVTP1, cRefVTAM.decimals(), 0d)) {
               XPVTP1 = 1d;
            }
            VATBaseAmount = mvxHalfAdjust((double)RVAT[IX - 1] / XPVTP1, cRefVTAM.decimals()); 	 	 	 	 	
            if (isBlank(GAMT[IX - 1], cRefCUAM.decimals()) || 	 	
               !isBlank(GAMT[IX - 1], cRefCUAM.decimals()) && 	 	 	
               !equals(GAMT[IX - 1], cRefCUAM.decimals(), VATBaseAmount)) {	 	 	 	 	
               clearingAmount = VATBaseAmount; 	 	 	 	 	 	 	
               clearingAmount -= GAMT[IX - 1]; 	 	 	 	 	 	 	 	
               writeSplitClearAcc(); 	 	 	 	
            } 	 	 	 	
         } else { 	 	 	
            if (!isBlank(GAMT[IX - 1], cRefCUAM.decimals())) { 	 	 	
               clearingAmount = GAMT[IX - 1]; 	 	 	
               writeSplitClearAcc(); 	 	 	 	 	 	
               writeVATRecord();
               //  Accounting dimensions would change inside 	
               //  writeVATRecord(), return dimensions of clearing account 	
               //  since system would create clearing record in writeSplitClearAcc() 	
               CR040.setACA()[0].moveLeftPad(XXAIT1); 	
               CR040.setACA()[1].moveLeftPad(XXAIT2); 	
               CR040.setACA()[2].moveLeftPad(XXAIT3); 	
               CR040.setACA()[3].moveLeftPad(XXAIT4); 	
               CR040.setACA()[4].moveLeftPad(XXAIT5); 	
               CR040.setACA()[5].moveLeftPad(XXAIT6); 	
               CR040.setACA()[6].moveLeftPad(XXAIT7); 	
            } 	 	 	 	 	 	
         } 	 	 	
      } 	 	 	 	
      // If we have something left to distribute, we create a clearing account record 	 	 	 	
      // with that amount and the VAT code set to zero 	 	 	 	
      if (!isBlank(XTCUAM, cRefCUAM.decimals())) { 	 	 	 	
         noVATClearAcc = true;
         unbalancedVoucher = true;
         clearingAmount = -(XTCUAM); 	 	 	 	 	
         writeSplitClearAcc(); 	 	 	 	
      } 	 	 	 	
   } 	 	 	

   /**
   * Read FCR040 to fill the VAT arrays and if invoice matching also FGINAE
   */
   public void fillVATArrays() { 	 	 	
      clear(RVAT); 	 	 	 	
      clear(VTP1);
      clear(VTD1);
      //  First read FCR040 to get already accounted VAT amount 	 	 	
      //  for each VAT code 	 	 	
      CR040.setSTCF(0); 	 	 	
      CR040.setJBNO(GLS040_JBNO.getInt()); 	 	 	
      CR040.setJBDT(GLS040_JBDT.getInt()); 	 	 	
      CR040.setJBTM(GLS040_JBTM.getInt()); 	 	 	
      CR040.setBCHN(currentBCHN); 	 	
      CR040.SETLL("00", CR040.getKey("00", 5)); 	 	 	
      IN93 = !CR040.READE("00", CR040.getKey("00", 5)); 	 	 	
      while (!IN93) {
         if (CR040.getVTCD() != 0) {
            if ((CR040.getAT04() == 1 || CR040.getAT04() == 2) && 	 	 	
                (XXEUVT != 1 || 	 	 	
                 XXEUVT == 1 && greaterThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d))) {
               // Look for deductable VAT
               getVATRate(CR040.getVTCD());
               if (!isBlank(VATPC.getVTD1(), EPS_2)) {
                  deductableVAT = true;
               }
               IX = CR040.getVTCD(); 	 	 	
               // Recorded VAT amount  	 	 	
               RVAT[IX - 1] += CR040.getCUAM(); 	 	 	
               VTP1[IX - 1] = CR040.getVTP1();
               // If deductable VAT save deductable percent in VTD1
               if (deductableVAT) {
                  VTD1[IX - 1] = VATPC.getVTD1();
               }
            }      
         } 	 	 	
         IN93 = !CR040.READE("00", CR040.getKey("00", 5)); 	 	 	
      } 	 	
      //   Invoice matching 	 	
      clear(GAMT); 	 	
      if (APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING() || 	 	
          APIBH.getIMCD() == cRefIMCDext.PO_HEAD_MATCHING() && 	 	
         !APIBH.getPUNO().isBlank()) { 	 	
         clear(GAMT); 	 	
         //  Then read FGINLI to get net goods value for each VAT code 	 	 	 	 	
         GINLI.setCONO(APIBH.getCONO()); 
         GINLI.setDIVI().moveLeftPad(APIBH.getDIVI());      
         GINLI.setSUNO().moveLeftPad(APIBH.getSUNO());       
         GINLI.setSINO().moveLeftPad(APIBH.getSINO());      
         GINLI.setINYR(CR040.getINYR()); 
         GINLI.SETLL("00", GINLI.getKey("00", 5));   
         while (GINLI.READE("00", GINLI.getKey("00", 5))) {      
            if ((GINLI.getINLP() == 1 ||   
                 GINLI.getINLP() == 2 ||   
                 GINLI.getINLP() == 6)  && 	
                GINLI.getVTCD() != 0) {   
               IX = GINLI.getVTCD();   
               // Net goods value      
               if (APIBH.getIMCD() != cRefIMCDext.PO_HEAD_MATCHING()) {    
                  GAMT[IX - 1] += GINLI.getIVNA();    
               }  
            }        
         } 	 	 	 	
      } 	 	 	 	
   } 	 	 	

   /**
   * Write splitted clearing account transactions to FCR040
   */
   public void writeSplitClearAcc() { 	 	 	 	
      if (lessThan(clearingAmount, cRefCUAM.decimals(), 0d)) { 	 	
         X1CUAM = -(clearingAmount); 	 	 	 	
      } else { 	 	 	 	 	 	
         X1CUAM = clearingAmount; 	 	 	
      } 	 	 	 	 	
      if (!noVATClearAcc) { 	 	 	 	
         XXVTCD = IX; 	 	 	
      } else { 	 	 	 	
         if (!multipleVTCD &&
            computeVAT && !unbalancedVoucher) { 	 	
            XXVTCD = CR040.getVTCD(); 	
         } else { 	
            XXVTCD = 0; 
            if (greaterThan(X1CUAM, 2, 0d) &&
               !unbalancedVoucher &&
               computeVAT || writeVATRecord) { 	
               X1CUAM = -(X1CUAM); 	
            } 	
         }
      }      
      CR040.setTRCD(41); 	 	 	
      CR040.setAPRV(XXAPRV); 	 	 	
      // Clear some fields in FCR040 	 	 	
      CLR04(); 	 	 	
      if (APIBH.getCUCD().NE(LDAZD.LOCD) && 	 	 	
          APIBH.getDIVI().EQ(LDAZD.DIVI) || 	 	
          APIBH.getCUCD().NE(MNDIV.getLOCD()) && 	 	 	
          APIBH.getDIVI().NE(LDAZD.DIVI)) { 	 	 	
         PLCLCCU.FZCUAM = X1CUAM; 	 	 	
         PLCLCCU.FZACAM = 0d; 	 	 	
         PLCLCCU.FZARAT = APIBH.getARAT(); 	 	 	
         IN92 = PLCLCCU.CCLCCUR(); 	 	 	
         XXACAM = PLCLCCU.FZACAM; 	 	
      } else { 	 	 	
         XXACAM = X1CUAM; 	 	 	
         PLCLCCU.FZARAT = APIBH.getARAT(); 	 	 	
         PLCLCCU.FZRAFA = 0; 	 	 	
         PLCLCCU.FZCUAM = 0d; 	 	 	
         PLCLCCU.FZACAM = 0d; 	 	 	
         PLCLCCU.FZTEST = 0; 	 	 	
         PLCLCCU.FZDCAM = 0; 	 	 	
         PLCLCCU.FZLCDC = 0; 	 	 	
         PLCLCCU.FZTX15.clear(); 	 	 	
         PLCLCCU.FZVERR = 0; 	 	 	
         PLCLCCU.FZMSGI.clear(); 	 	 	
         PLCLCCU.FZMSGN = 0; 	 	 	
         PLCLCCU.FZMSGA.clear(); 	 	 	
         IN92 = PLCLCCU.CCLCCUR(); 	 	 	
      } 	 	 	
      XXCUAM = X1CUAM; 	 	
      XXVTAM = 0d; 	 	
      //   Invoice matching 	 	
      if (APIBH.getIMCD() == cRefIMCDext.PO_LINE_MATCHING()) { 	 	
         CR040.setPINC(4); 	 	
      } else { 	 	
         CR040.setPINC(0); 	 	
      } 	 	
      ADAT04(); 	 	
      if (CR040.getAT04() >= 3 && CR040.getAT04() <= 9 && XXVTCD != 0) { 	 	
         PLCRTVT.FTCONO = APIBH.getCONO(); 	 	
         PLCRTVT.FTDIVI.move(APIBH.getDIVI()); 	 	
         PLCRTVT.FTCMTP = LDAZD.CMTP; 	 	
         PLCRTVT.FTTASK = 1; 	 	
         PLCRTVT.FTVATH = 1; 	 	
         PLCRTVT.FTVTCD = XXVTCD; 	 	
         PLCRTVT.FTACDT = APIBH.getIVDT(); 	 	
         PLCRTVT.FTLCDC = X1LCDC; 	 	
         PLCRTVT.FTBAAM = XXCUAM;
         PLCRTVT.FTCUCD.move(APIBH.getCUCD()); 	 	
         PLCRTVT.FTEVEN.move(XXEVEN); 	 	
         PLCRTVT.FTTECD.move(APIBH.getTECD()); 	 	
         if (!APIBH.getSPYN().isBlank()) { 	 	
            PLCRTVT.FTSUNO.move(APIBH.getSPYN()); 	 	
         } else { 	 	
            PLCRTVT.FTSUNO.move(APIBH.getSUNO()); 	 	
         } 	 	
         PLCRTVT.FTCUNO.clear(); 	 	
         PLCRTVT.FTCSCD.move(XXBSCD); 	 	
         PLCRTVT.FTECAR.move(XXECAR); 	 	
         if (XXEUVT == 1) { 	
            PLCRTVT.FTIOCD = 3; 	
         } else { 	 	
            PLCRTVT.FTIOCD = 2;
            sendFromToCountryAsInput();
         } 	 	
         PLCRTVT.FTAI11.clear(); 	 	
         XVECAR.move(XXECAR); 	 	
         XVFTCO.move(XXFTCO); 	 	
         XVBSCD.move(XXBSCD); 	 	
         PLCRTVT.FTAI11.moveLeft(XVCFI1); 	 	
         PLCRTVT.CCRTVAT(); 	 	
      } else { 	 	
         PLCRTVT.FTVTM1 = 0D; 	 	
         PLCRTVT.FTVTM2 = 0D; 	 	
      } 	 	
      XXSUNO.move(CR040.getSUNO()); 	 	
      XXSINO.moveLeft(CR040.getSINO()); 	 	
      XXINYR.move(CR040.getINYR()); 	 	
      XXALSU.moveLeft(IDMAS.getALSU()); 	 	
      CR040.setVTXT().moveLeft(XXVTXT);
      // Set VAT code if no existing VAT line
      if (!hasVATLine) {
         XXVTCD = CR040.getVTCD();
      }
      CR040.setVTCD(XXVTCD); 	 	
      IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15)); 	 	
      if (IN91) {
         //  Deduct VAT amount if User entered Net VAT amount 	
         if (equals(APIBH.getCUAM(), 2, X1CUAM) && 	
             !multipleVTCD && computeVAT) { 	
            XXCUAM -= PLCRTVT.FTVTM1; 	
            XXCUAM -= PLCRTVT.FTVTM2; 	
            if (APIBH.getCUCD().NE(LDAZD.LOCD) && 	
               APIBH.getDIVI().EQ(LDAZD.DIVI) || 	
               APIBH.getCUCD().NE(MNDIV.getLOCD()) && 	
               APIBH.getDIVI().NE(LDAZD.DIVI)) { 	
               PLCLCCU.FZCUAM = XXCUAM; 	
               PLCLCCU.FZACAM = 0d; 	
               PLCLCCU.FZARAT = APIBH.getARAT(); 	
               IN92 = PLCLCCU.CCLCCUR(); 	
               XXACAM = PLCLCCU.FZACAM; 	
            } else { 	
               XXACAM = XXCUAM; 	
            } 	
         }
         XXTRNO++; 	 	
         CR040.setTRNO(XXTRNO); 	 	
         CR040.setCUAM(XXCUAM); 	 	
         CR040.setACAM(XXACAM); 	 	
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setVTAM(XXVTAM); 	 	
            CR040.setCVT1(PLCRTVT.FTVTM1); 	 	
            CR040.setCVT2(PLCRTVT.FTVTM2); 	 	
            CR040.setVTP1(PLCRTVT.FTVTP1); 	 	
            CR040.setVTP2(PLCRTVT.FTVTP2); 	 	
         } else { 	 	
            CR040.setVTAM(0d); 	 	
         } 	 	
         CR040.setDEDA(APIBH.getDEDA()); 	 	
         CR040.setARAT(PLCLCCU.FZARAT); 	 	
         CR040.setDBCR(' '); 	 	
         if (CRS750DS.getPBDCNY() == 1) { 	 	
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) { 	 	
               CR040.setDBCR(CRS750DS.getPBDBNG()); 	 	
            } else { 	 	
               CR040.setDBCR(CRS750DS.getPBDBPS()); 	 	
            } 	 	
         } 	 	
         //    Entry method 	 	
         if (APIBH.getENME() == 1) { 	 	
            CR040.setENME(1);
         } else {
            CR040.setENME(0);
         } 	 	
         //   Accumulate for test of unbalanced voucher 	 	
         XTACAM += CR040.getACAM(); 	 	
         XTCUAM += CR040.getCUAM(); 	
         CR040.setIVCL().moveLeftPad(invoiceClass);
         CR040.WRITE("10"); 	 	
      } else { 	 	
         CR040.setCUAM(CR040.getCUAM() + XXCUAM); 	 	
         CR040.setACAM(CR040.getACAM() + XXACAM); 	 	
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setVTAM(XXVTAM); 	 	
            CR040.setCVT1(CR040.getCVT1() + PLCRTVT.FTVTM1); 	 	
            CR040.setCVT2(CR040.getCVT2() + PLCRTVT.FTVTM2); 	 	
         } else { 	 	
            CR040.setVTAM(0d); 	 	
         } 	
         CR040.setDBCR(' '); 	 	
         if (CRS750DS.getPBDCNY() == 1) { 	 	
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) { 	 	
               CR040.setDBCR(CRS750DS.getPBDBNG()); 	 	
            } else { 	 	
               CR040.setDBCR(CRS750DS.getPBDBPS()); 	 	
            } 	 	
         } 	 	
         //   Accumulate for test of unbalanced voucher 	 	
         XTACAM += XXACAM; 	 	
         XTCUAM += XXCUAM; 	 	
         CR040.UPDAT("10"); 	 	
      } 	 	
      CR040.setVTXT().move(APS455DS.getZWVTXT()); 	 	
   }    	 	 	 	 	

   /**
   * Write VAT account transactions to FCR040
   */
   public void writeVATRecord() {
      writeVATRecord = false;
      CR040.setTRCD(41); 	 	 	
      CR040.setAPRV(XXAPRV); 	 	 	
      CR040.setFTCO().move(XXFTCO); 	 	 	
      CR040.setBSCD().move(XXBSCD); 	 	 	
      CR040.setVTCD(XXVTCD); 	 	 	
      PLCRTVT.FTCONO = APIBH.getCONO(); 	 	 	
      PLCRTVT.FTDIVI.move(APIBH.getDIVI()); 	 	 	
      PLCRTVT.FTCMTP = LDAZD.CMTP; 	 	 	
      PLCRTVT.FTTASK = 3; 	 	 	
      PLCRTVT.FTVATH = 1; 	 	
      PLCRTVT.FTVTCD = XXVTCD; 	 	 	
      PLCRTVT.FTACDT = APIBH.getIVDT(); 	 	 	
      PLCRTVT.FTLCDC = X1LCDC; 	 	 	
      PLCRTVT.FTBAAM = (X1CUAM); 	 	 	 	 	
      PLCRTVT.FTCUCD.move(APIBH.getCUCD()); 	 	 	
      PLCRTVT.FTEVEN.move(XXEVEN); 	 	 	
      PLCRTVT.FTTECD.move(APIBH.getTECD()); 	 	 	
      if (!APIBH.getSPYN().isBlank()) { 	 	 	
         PLCRTVT.FTSUNO.move(APIBH.getSPYN()); 	 	 	
      } else { 	 	 	
         PLCRTVT.FTSUNO.move(APIBH.getSUNO()); 	 	 	
      } 	 	 	
      PLCRTVT.FTCUNO.clear(); 	 	 	
      PLCRTVT.FTCSCD.move(XXBSCD); 	 	 	
      PLCRTVT.FTECAR.move(XXECAR); 	 	 	
      if (XXEUVT == 1) { 	 	 	
         PLCRTVT.FTIOCD = 3; 	 	 	
      } else { 	 	 	
         PLCRTVT.FTIOCD = 2;
         sendFromToCountryAsInput();
      } 	 	 	
      PLCRTVT.FTAI11.clear(); 	
      XVECAR.move(XXECAR); 	 	 	
      XVFTCO.move(XXFTCO); 	 	 	
      XVBSCD.move(XXBSCD); 	 	 	
      PLCRTVT.FTAI11.moveLeft(XVCFI1); 	 	 	
      PLCRTVT.CCRTVAT(); 	 	 	
      if (!isBlank(PLCRTVT.FTVTM1, cRefVTAM.decimals())) {
         if (automaticVATaccounting_AVAT != 4) { 	
            writeVATRecord = true; 	
         }
         XCVTA1 = PLCRTVT.FTVTM1; 	 	 	
         XCVTA2 = PLCRTVT.FTVTM2; 	 	 	
         CR040.setVTP1(PLCRTVT.FTVTP1); 	 	 	
         CR040.setVTP2(PLCRTVT.FTVTP2); 	 	 	
         CR040.setVTAM(0d); 	 	 	
         XXVTAM = 0d; 	 	 	
         XXVTAM += PLCRTVT.FTVTM1; 	 	 	
         XXVTAM += PLCRTVT.FTVTM2; 	 	 	
         if (APIBH.getCUCD().NE(LDAZD.LOCD) && 	 	 	
             APIBH.getDIVI().EQ(LDAZD.DIVI) || 	 	 	
             APIBH.getCUCD().NE(MNDIV.getLOCD()) && 	 	 	
             APIBH.getDIVI().NE(LDAZD.DIVI)) { 	 	 	
            PLCLCCU.FZCUAM = XCVTA1; 	 	 	
            PLCLCCU.FZACAM = 0d; 	 	 	
            PLCLCCU.FZARAT = APIBH.getARAT(); 	 	 	
            PLCLCCU.CCLCCUR(); 	 	 	
            XAVTA1 = PLCLCCU.FZACAM; 	 	 	
         } else { 	 	 	
            XAVTA1 = XCVTA1; 	 	 	
            PLCLCCU.FZARAT = APIBH.getARAT(); 	 	 	
            PLCLCCU.FZRAFA = 0; 	 	 	
            PLCLCCU.FZCUAM = 0d; 	 	 	
            PLCLCCU.FZACAM = 0d; 	 	 	
            PLCLCCU.FZTEST = 0; 	 	 	
            PLCLCCU.FZDCAM = 0; 	 	
            PLCLCCU.FZLCDC = 0; 	 	 	
            PLCLCCU.FZTX15.clear(); 	 	
            PLCLCCU.FZVERR = 0; 	 	 	
            PLCLCCU.FZMSGI.clear(); 	 	 	
            PLCLCCU.FZMSGN = 0; 	 	 	
            PLCLCCU.FZMSGA.clear(); 	 	 	
            IN92 = PLCLCCU.CCLCCUR(); 	 	 	
         } 	 	 	
         if (APIBH.getCUCD().NE(LDAZD.LOCD) && 	 	 	
             APIBH.getDIVI().EQ(LDAZD.DIVI) || 	 	 	
             APIBH.getCUCD().NE(MNDIV.getLOCD()) && 	 	 	
             APIBH.getDIVI().NE(LDAZD.DIVI)) { 	 	
            PLCLCCU.FZCUAM = XCVTA2; 	 	 	
            PLCLCCU.FZACAM = 0d; 	 	 	
            PLCLCCU.FZARAT = APIBH.getARAT(); 	 	 	
            PLCLCCU.CCLCCUR(); 	 	 	
            XAVTA2 = PLCLCCU.FZACAM; 	 	 	
         } else { 	 	 	
            XAVTA2 = XCVTA2; 	 	 	
            PLCLCCU.FZARAT = APIBH.getARAT(); 	 	 	
            PLCLCCU.FZRAFA = 0; 	 	 	
            PLCLCCU.FZCUAM = 0d; 	 	 	
            PLCLCCU.FZACAM = 0d; 	 	 	
            PLCLCCU.FZTEST = 0; 	 	 	
            PLCLCCU.FZDCAM = 0; 	 	 	
            PLCLCCU.FZLCDC = 0; 	 	 	
            PLCLCCU.FZTX15.clear(); 	 	 	
            PLCLCCU.FZVERR = 0; 	 	 	
            PLCLCCU.FZMSGI.clear(); 	 	 	
            PLCLCCU.FZMSGN = 0; 	 	 	
            PLCLCCU.FZMSGA.clear(); 	 	 	
            IN92 = PLCLCCU.CCLCCUR(); 	 	 	
         } 	 	 	
         CR040.setARAT(PLCLCCU.FZARAT); 	 	 	
         CR040.setAIT1().move(PLCRTVT.FTAI11); 	 	 	
         CR040.setAIT2().move(PLCRTVT.FTAI12); 	 	 	
         CR040.setAIT3().move(PLCRTVT.FTAI13); 	
         CR040.setAIT4().move(PLCRTVT.FTAI14); 	 	 	
         CR040.setAIT5().move(PLCRTVT.FTAI15); 	 	
         CR040.setAIT6().move(PLCRTVT.FTAI16); 	 	 	
         CR040.setAIT7().move(PLCRTVT.FTAI17); 	 	 	
         ADAT04(); 	 	 	
         IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15)); 	 	 	
         if (IN91) { 	 	 	
            CR040.setCUAM(XCVTA1); 	 	 	
            CR040.setACAM(XAVTA1); 	 	 	
            if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
               CR040.setVTAM(XXVTAM); 	 	 	
            } else { 	 	 	
               CR040.setVTAM(0d); 	 	 	
            } 	 	 	
            CR040.setCVT1(0d); 	 	 	
            CR040.setCVT2(0d); 	 	 	
            CR040.setDEDA(APIBH.getDEDA()); 	 	 	
            CR040.setARAT(PLCLCCU.FZARAT); 	 	 	
            XXTRNO++; 	 	 	
            CR040.setTRNO(XXTRNO); 	 	 	
            CR040.setDBCR(' '); 	 	 	
            if (CRS750DS.getPBDCNY() == 1) { 	 	 	
               if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) { 	 	 	
                  CR040.setDBCR(CRS750DS.getPBDBNG()); 	
               } else { 	 	 	
                  CR040.setDBCR(CRS750DS.getPBDBPS()); 	 	 	
               } 	 	 	
            } 	 	
            //    Entry method 	 	 	
            if (APIBH.getENME() == 1) { 	 	 	
               CR040.setENME(1);
            } else {
               CR040.setENME(0);
            } 	 	
            CR040.setIVCL().moveLeftPad(invoiceClass);
            CR040.WRITE("10"); 	 	 	
         } else { 	 	 	
            CR040.setCUAM(CR040.getCUAM() + XCVTA1); 	 	 	
            CR040.setACAM(CR040.getACAM() + XAVTA1); 	 	 	
            if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
               CR040.setVTAM(CR040.getVTAM() + XXVTAM); 	 	 	
            } else { 	 	 	
               CR040.setVTAM(0d); 	 	 	
            } 	 	 	
            CR040.setDBCR(' '); 	 	 	
            if (CRS750DS.getPBDCNY() == 1) { 	 	 	
               if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) { 	 	 	
                  CR040.setDBCR(CRS750DS.getPBDBNG()); 	 	 	
               } else { 	 	 	
                  CR040.setDBCR(CRS750DS.getPBDBPS()); 	 	 	
               } 	 	 	
            } 	 	 	
            CR040.UPDAT("10"); 	 	 	
         } 	 	 	
         //   Accumulate for test of unbalanced voucher 	 	 	
         XTACAM += XAVTA1; 	 	 	
         XTCUAM += XCVTA1; 	 	 	
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            XXVTAM = -(XXVTAM); 	 	 	
         } 	 	 	
         if (!isBlank(PLCRTVT.FTVTM2, cRefVTAM.decimals())) { 	 	 	
            CR040.setAIT1().move(PLCRTVT.FTAI21); 	 	 	
            CR040.setAIT2().move(PLCRTVT.FTAI22); 	 	 	
            CR040.setAIT3().move(PLCRTVT.FTAI23); 	 	 	
            CR040.setAIT4().move(PLCRTVT.FTAI24); 	 	 	
            CR040.setAIT5().move(PLCRTVT.FTAI25); 	 	 	
            CR040.setAIT6().move(PLCRTVT.FTAI26); 	 	 	
            CR040.setAIT7().move(PLCRTVT.FTAI27); 	 	 	
            ADAT04(); 	 	 	
            IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15)); 	 	 	
            if (IN91) { 	 	 	
               CR040.setCUAM(XCVTA2); 	 	 	
               CR040.setACAM(XAVTA2); 	 	 	
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTAM(XXVTAM); 	 	 	
               } else { 	 	 	
                  CR040.setVTAM(0d); 	 	 	
               } 	 	 	
               CR040.setCVT1(0d); 	 	 	
               CR040.setCVT2(0d); 	 	 	
               CR040.setDEDA(APIBH.getDEDA()); 	 	 	
               XXTRNO++; 	 	 	
               CR040.setTRNO(XXTRNO); 	 	 	
               CR040.setDBCR(' '); 	 	 	
               if (CRS750DS.getPBDCNY() == 1) { 	 	 	
                  if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) { 	 	 	
                     CR040.setDBCR(CRS750DS.getPBDBNG()); 	 	 	
                  } else { 	 	 	
                     CR040.setDBCR(CRS750DS.getPBDBPS()); 	 	 	
                  } 	 	 	
               } 	 	 	
               CR040.setIVCL().moveLeftPad(invoiceClass);
               CR040.WRITE("10"); 	 	 	
            } else { 	 	 	
               CR040.setCUAM(CR040.getCUAM() + XCVTA2); 	 	
               CR040.setACAM(CR040.getACAM() + XAVTA2); 	 	 	
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTAM(CR040.getVTAM() + XXVTAM); 	 	
               } else { 	 	 	
                  CR040.setVTAM(0d); 	 	 	
               } 	 	 	
               CR040.setDBCR(' '); 	 	 	
               if (CRS750DS.getPBDCNY() == 1) { 	 	 	
                  if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) { 	 	 	
                     CR040.setDBCR(CRS750DS.getPBDBNG()); 	 	 	
                  } else { 	 	 	
                     CR040.setDBCR(CRS750DS.getPBDBPS()); 	 	 	
                  } 	 	
               } 	 	 	
               CR040.UPDAT("10"); 	 	 	
            } 	 	 	
            //   Accumulate for test of unbalanced voucher 	 	 	
            XTACAM += XAVTA2; 	 	 	
            XTCUAM += XCVTA2; 	 	 	
         } 	 	 	
      } 	 	 	
   } 	 	 	 	 	 	 	

  /**
   *  getVATRate - Check in CVATPC for correct VAT rate
   */
   public void getVATRate(int VTCD) {
      if (VTCD != previous_VTCD) {
         previous_VTCD = VTCD;
         deductableVAT = false;
        // 1A) Search for VAT rate, with Division, Country and Area    
        VATPC.setCONO(APIBL.getCONO());
        VATPC.setDIVI().move(APIBL.getDIVI());
        VATPC.setVTCD(VTCD);
        VATPC.setCSCD().move(XXFTCO);
        VATPC.setECAR().move(XXECAR);
        VATPC.setFRDT(getIntMax(8));
        VATPC.SETLL("00", VATPC.getKey("00"));
        IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
        while (!IN93) {
           if (VATPC.getDIVI().EQ(APIBL.getDIVI()) &&    
               VATPC.getVTCD() == VTCD  &&    
               VATPC.getCSCD().EQ(XXFTCO) &&
               VATPC.getECAR().EQ(XXECAR) &&
               VATPC.getFRDT() <= currentACDT) {
              return;
           }
           IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
        }
        // 1B) Search for VAT rate, with Division=blank, Country and Area       
        if (LDAZD.CMTP == 2 &&       
            !APIBL.getDIVI().isBlank()) {       
           VATPC.setDIVI().clear();  
           VATPC.setFRDT(getIntMax(8));     
           VATPC.SETLL("00", VATPC.getKey("00"));       
           IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));     
           while (!IN93) {      
              if (VATPC.getDIVI().isBlank() &&       
                  VATPC.getVTCD() == VTCD  &&       
                  VATPC.getCSCD().EQ(XXFTCO) &&       
                  VATPC.getECAR().EQ(XXECAR) &&       
                  VATPC.getFRDT() <= currentACDT) {     
                 return;     
              }     
              IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));     
           }     
        }  
        // 2A) Search for VAT rate, with Division, Country       
        //     and Area=blank     
        VATPC.setDIVI().move(APIBL.getDIVI());
        VATPC.setECAR().clear();     
        VATPC.setFRDT(getIntMax(8));     
        VATPC.SETLL("00", VATPC.getKey("00"));
        IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
        while (!IN93) {
           if (VATPC.getDIVI().EQ(APIBL.getDIVI()) &&    
               VATPC.getVTCD() == VTCD  &&    
               VATPC.getCSCD().EQ(XXFTCO) &&
               VATPC.getECAR().isBlank() &&
               VATPC.getFRDT() <= currentACDT) {
              return;
           }
           IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
        }
        // 2B) Search for VAT rate, with Division=blank, Country       
        //     and Area=blank     
        if (LDAZD.CMTP == 2 &&       
            !APIBL.getDIVI().isBlank()) {       
           VATPC.setDIVI().clear();     
           VATPC.setFRDT(getIntMax(8));     
           VATPC.SETLL("00", VATPC.getKey("00"));
           IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
           while (!IN93) {
              if (VATPC.getDIVI().isBlank() &&       
                  VATPC.getVTCD() == VTCD  &&       
                  VATPC.getCSCD().EQ(XXFTCO) &&
                  VATPC.getECAR().isBlank() &&
                  VATPC.getFRDT() <= currentACDT) {              
                 return;
              }
              IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
           }
        }  
        // 3A) Search for VAT rate, with Division, Country=blank       
        //     and Area=blank     
        VATPC.setDIVI().move(APIBL.getDIVI());       
        VATPC.setCSCD().clear();     
        VATPC.setFRDT(getIntMax(8));     
        VATPC.SETLL("00", VATPC.getKey("00"));
        IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
        while (!IN93) {
           if (VATPC.getDIVI().EQ(APIBL.getDIVI()) &&       
               VATPC.getVTCD() == VTCD  &&       
               VATPC.getCSCD().isBlank() &&
               VATPC.getECAR().isBlank() &&
               VATPC.getFRDT() <= currentACDT) {
              return;
           }
           IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
        }
        // 3B) Search for VAT rate, with Division=blank, Country=blank       
        //     and Area=blank     
        if (LDAZD.CMTP == 2 &&       
            !APIBL.getDIVI().isBlank()) {       
           VATPC.setDIVI().clear();     
           VATPC.setFRDT(getIntMax(8));     
           VATPC.SETLL("00", VATPC.getKey("00"));
           IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
           while (!IN93) {
              if (VATPC.getDIVI().isBlank() &&       
                  VATPC.getVTCD() == VTCD  &&       
                  VATPC.getCSCD().isBlank() &&
                  VATPC.getECAR().isBlank() &&
                  VATPC.getFRDT() <= currentACDT) {              
                 return;
              }
              IN93 = !VATPC.REDPE("00", VATPC.getKey("00", 5));
           }
        }
        // If no rate found clear deductable rate fields
        VATPC.setVTD1(0d);
        VATPC.setVTD2(0d);
      }  
   }   
   
   /**
   *  recalculateVAT - Calculate deductable amount with Deductable percent
   */
   public void calculateDeductable(double amount, double percent) {
      XVVTDX = 100d - percent;
      XXVTDX = mvxHalfAdjust((double)XVVTDX/100d, 4);
      XXVTMX = amount;
      // XXDDVA = The new VAT amount after deduction
      XXDDVA = XXVTDX * XXVTMX;
      // XDVTMX = Deduction amount
      XDVTMX = XXVTMX - XXDDVA;      
   }
   
   /**
   * Update FCR040 with Claim against Supplier
   */
   public void claimAgainstSupplier() {
      //Cleare some fields in FCR040
      CLR04(); 
      PSUPR.setCONO(APIBH.getCONO());
      PSUPR.setDIVI().move(APIBH.getDIVI());
      PSUPR.setINBN(APIBH.getINBN());
      PSUPR.SETLL("50", PSUPR.getKey("50", 3));
      //   Read records
      while (PSUPR.READE_LOCK("50", PSUPR.getKey("50", 3))) {
         if (!isBlank(PSUPR.getSCAM(), cRefSCAM.decimals())) {
            CR040.setTRCD(41);
            CR040.setAPRV(XXAPRV);
            //    Set params for CRTVACC (Supplier invoice batch - Claim against supplier) 	
            PXCRTVAC.PPCONO = APIBH.getCONO(); 	
            PXCRTVAC.PPCMTP = LDAZD.CMTP; 	
            PXCRTVAC.PPLANC.move(LDAZD.LANC); 	
            PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM); 	
            PXCRTVAC.PPDIVI.move(APIBH.getDIVI()); 	
            PXCRTVAC.PPEVEN.move("AP50"); 	
            PXCRTVAC.PPACTY.move("420 "); 	
            PXCRTVAC.PPFDAT = currentACDT; 
            if (PSUPR.getORCA().EQ("311")){   
               PXCRTVAC.PPRORC = 3;   
               PXCRTVAC.PPDLIX = PSUPR.getRIDI(); 	
            } else {
               if (PSUPR.getORCA().EQ("771")){   
                  PXCRTVAC.PPRORC = 7;
                  PXCRTVAC.PPDLIX = PSUPR.getALI1(); 	
               }
            }   
            PXCRTVAC.PPORNO.move(PSUPR.getRIDN()); 	
            PXCRTVAC.PPPONR = PSUPR.getRIDL(); 	
            PXCRTVAC.PPPOSX = PSUPR.getRIDX(); 	
            PXCRTVAC.PPWHLO.move(PSUPR.getWHLO()); 	
            PXCRTVAC.PPEXIN.moveLeft(PSUPR.getEXIN()); 
            PXCRTVAC.PPYEA4 = PSUPR.getYEA4();
            MoveInvoiceNo.CONO = APIBH.getCONO(); 	 	 	 	 	 	
            MoveInvoiceNo.DIVI.moveLeftPad(APIBH.getDIVI()); 	 	 	 	 	 	
            MoveInvoiceNo.CINO.moveLeftPad(PSUPR.getEXIN()); 	 	 	 	 	 	
            MoveInvoiceNo.YEA4 = PSUPR.getYEA4(); 	 	 	 	 	 	
            MoveInvoiceNo.toIVNO(); 
            PXCRTVAC.PPINPX.moveLeftPad(MoveInvoiceNo.INPX);
            PXCRTVAC.PPIVNO = ((int)MoveInvoiceNo.IVNO);
            XVECAR.moveLeft(XXECAR);
            XVBSCD.moveLeft(XXBSCD);
            XVFTCO.moveLeft(XXFTCO);
            PXCRTVAC.PPCFI1.moveLeft(XVCFI1);
            //    Create accounting references
            IN92 = PXCRTVAC.CRTVACC();
            CR040.setAIT1().move(PXCRTVAC.PPAIT1);
            CR040.setAIT2().move(PXCRTVAC.PPAIT2);
            CR040.setAIT3().move(PXCRTVAC.PPAIT3);
            CR040.setAIT4().move(PXCRTVAC.PPAIT4);
            CR040.setAIT5().move(PXCRTVAC.PPAIT5);
            CR040.setAIT6().move(PXCRTVAC.PPAIT6);
            CR040.setAIT7().move(PXCRTVAC.PPAIT7);
            //   - Fetch amount in local currency
            if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                APIBH.getDIVI().NE(LDAZD.DIVI)) {
               PLCLCCU.FZCUAM = -(PSUPR.getSCAM());
               PLCLCCU.FZACAM = 0d;
               PLCLCCU.FZARAT = APIBH.getARAT();
               if (PSUPR.getARAT() != 0) {
                  PLCLCCU.FZARAT = PSUPR.getARAT();
					}
               PLCLCCU.CCLCCUR();
               XXACAM = PLCLCCU.FZACAM;
            } else {
               XXACAM = -(PSUPR.getSCAM());
               PLCLCCU.FZARAT = APIBH.getARAT();
               PLCLCCU.FZRAFA = 0;
               PLCLCCU.FZCUAM = 0d;
               PLCLCCU.FZACAM = 0d;
               PLCLCCU.FZTEST = 0;
               PLCLCCU.FZDCAM = 0;
               PLCLCCU.FZLCDC = 0;
               PLCLCCU.FZTX15.clear();
               PLCLCCU.FZVERR = 0;
               PLCLCCU.FZMSGI.clear();
               PLCLCCU.FZMSGN = 0;
               PLCLCCU.FZMSGA.clear();
               IN92 = PLCLCCU.CCLCCUR();
            }
            XXCUAM = -(PSUPR.getSCAM());
            if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
               CR040.setVTCD(PSUPR.getVTCP());
            } else {
               CR040.setVTCD(0);
            }
            XXVTAM = 0d;
            ADAT04();
            if (CR040.getAT04() >= 3 &&
                CR040.getAT04() <= 9 &&
                CR040.getVTCD() != 0) {
               PLCRTVT.FTCONO = APIBH.getCONO();
               PLCRTVT.FTDIVI.move(APIBH.getDIVI());
               PLCRTVT.FTCMTP = LDAZD.CMTP;
               PLCRTVT.FTTASK = 1;
               PLCRTVT.FTVATH = 1;
               PLCRTVT.FTVTCD = CR040.getVTCD();
               PLCRTVT.FTACDT = APIBH.getIVDT();
               PLCRTVT.FTLCDC = X1LCDC;
               PLCRTVT.FTBAAM = XXCUAM;
               PLCRTVT.FTCUCD.move(APIBH.getCUCD());
               PLCRTVT.FTEVEN.move(XXEVEN);
               PLCRTVT.FTTECD.move(APIBH.getTECD());
               if (!APIBH.getSPYN().isBlank()) {
                  PLCRTVT.FTSUNO.move(APIBH.getSPYN());
               } else {
                  PLCRTVT.FTSUNO.move(APIBH.getSUNO());
               }
               PLCRTVT.FTCUNO.clear();
               PLCRTVT.FTCSCD.move(XXBSCD);
               PLCRTVT.FTECAR.move(XXECAR);
               if (XXEUVT == 1) {
                  PLCRTVT.FTIOCD = 3;
               } else {
                  PLCRTVT.FTIOCD = 2;
                  sendFromToCountryAsInput();
               }
               PLCRTVT.FTAI11.clear();
               XVECAR.move(XXECAR);
               XVFTCO.move(XXFTCO);
               XVBSCD.move(XXBSCD);
               PLCRTVT.FTAI11.moveLeft(XVCFI1);
               PLCRTVT.CCRTVAT();
            } else {
               PLCRTVT.FTVTM1 = 0D;
               PLCRTVT.FTVTM2 = 0D;
            }
            IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
            if (IN91) {
               XXTRNO++;
               CR040.setTRNO(XXTRNO);
               CR040.setCUAM(XXCUAM);
               CR040.setACAM(XXACAM);
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTAM(XXVTAM);
                  CR040.setCVT1(PLCRTVT.FTVTM1);
                  CR040.setCVT2(PLCRTVT.FTVTM2);
               } else {
                  CR040.setVTAM(0d);
               }
               CR040.setDEDA(APIBH.getDEDA());
               CR040.setARAT(PLCLCCU.FZARAT);
               CR040.setDBCR(' ');
               if (CRS750DS.getPBDCNY() == 1) {
                  if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                     CR040.setDBCR(CRS750DS.getPBDBNG());
                  } else {
                     CR040.setDBCR(CRS750DS.getPBDBPS());
                  }
               }
               //    Entry method
               if (APIBH.getENME() == 1) {
                  // Scanned invoice
                  CR040.setENME(1);
               } else {
                  CR040.setENME(0);

               }
               //   Accumulate for test of unbalanced voucher
               XTACAM += CR040.getACAM();
               XTCUAM += CR040.getCUAM();
               CR040.setIVCL().moveLeftPad(invoiceClass); 	
               CR040.WRITE("10");
            } else {
               CR040.setCUAM(CR040.getCUAM() + XXCUAM);
               CR040.setACAM(CR040.getACAM() + XXACAM);
               if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
                  CR040.setVTAM(XXVTAM);
                  CR040.setCVT1(CR040.getCVT1() + PLCRTVT.FTVTM1);
                  CR040.setCVT2(CR040.getCVT2() + PLCRTVT.FTVTM2);
               } else {
                  CR040.setVTAM(0d);
               }
               CR040.setDBCR(' ');
               if (CRS750DS.getPBDCNY() == 1) {
                  if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
                     CR040.setDBCR(CRS750DS.getPBDBNG());
                  } else {
                     CR040.setDBCR(CRS750DS.getPBDBPS());
                  }
               }
               //   Accumulate for test of unbalanced voucher
               XTACAM += XXACAM;
               XTCUAM += XXCUAM;
               CR040.UPDAT("10");
            }
            // Currency gains / Losses
				if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
                APIBH.getDIVI().EQ(LDAZD.DIVI) ||
                APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
                APIBH.getDIVI().NE(LDAZD.DIVI)) {
					if (PSUPR.getARAT() != 0 &&
                  !equals(PSUPR.getARAT(), 6, APIBH.getARAT())) {
						// calculate Diff amount and account for gains/losses using AP50-301/302
						claimAgainstSupplierCurrencyDifference();
					}
				}	 
            PSUPR.setCLDT(currentACDT);
            PSUPR.setSCTS().move(cRefSCTSext.UPDATED_IN_APL());
            PSUPR.UPDAT("50");
         } else {
            PSUPR.UNLOCK("50");
         }
      }
   }

  /**
   * Update FCR040 with Claim against Supplier - Currency differences
   */
   public void claimAgainstSupplierCurrencyDifference() {
		// Calculate currency difference amount
		double claimTransactionAmount = 0d;
		double invoiceTransactionAmount = 0d;
		double rateDifferenceAmount = 0d;
		// Claim amount based on 420 rate from customer order
		PLCLCCU.FZCUAM = PSUPR.getSCAM();
		PLCLCCU.FZACAM = 0d;
		PLCLCCU.FZARAT = PSUPR.getARAT();
		PLCLCCU.CCLCCUR();
		claimTransactionAmount = PLCLCCU.FZACAM;
      claimTransactionAmount = mvxHalfAdjust(claimTransactionAmount, cRefACAM.decimals());
		// Claim amount based on invoice rate
		PLCLCCU.FZCUAM = PSUPR.getSCAM();
		PLCLCCU.FZACAM = 0d;
		PLCLCCU.FZARAT = APIBH.getARAT();
		PLCLCCU.CCLCCUR();
		invoiceTransactionAmount = PLCLCCU.FZACAM;
      invoiceTransactionAmount = mvxHalfAdjust(invoiceTransactionAmount, cRefACAM.decimals());
		// Set difference - used as accounting value
		rateDifferenceAmount = claimTransactionAmount - invoiceTransactionAmount;
		// create accounting transaction
		if (!isBlank(rateDifferenceAmount, cRefACAM.decimals())) {
          //Cleare some fields in FCR040
         CLR04(); 
         //    Set params for CRTVACC (VAT accounting)
			PXCRTVAC.PPCONO = APIBH.getCONO();
			PXCRTVAC.PPDIVI.move(CR040.getDIVI());
			PXCRTVAC.PPEVEN.move("AP50");
			if (greaterThan(rateDifferenceAmount, cRefACAM.decimals(), 0d)) {
				PXCRTVAC.PPACTY.move("302 ");
			} else {
				PXCRTVAC.PPACTY.move("301 ");
			}
			PXCRTVAC.PPFDAT = CR040.getACDT();
			PXCRTVAC.PPCMTP = LDAZD.CMTP;
			PXCRTVAC.PPLANC.move(LDAZD.LANC);
			PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
			if (!CR040.getSPYN().isBlank()) {
				PXCRTVAC.PPSUNO.move(CR040.getSPYN());
			} else {
				PXCRTVAC.PPSUNO.move(CR040.getSUNO());
			}
			PXCRTVAC.PPCUCD.move(CR040.getCUCD());
			XVBSCD.moveLeft(CR040.getBSCD());
			XVFTCO.moveLeft(CR040.getFTCO());
			XVECAR.moveLeft(XXECAR);
			PXCRTVAC.PPCFI1.moveLeft(XVCFI1);
			//    Create accounting references
			IN92 = PXCRTVAC.CRTVACC();
			CR040.setAIT1().move(PXCRTVAC.PPAIT1);
			CR040.setAIT2().move(PXCRTVAC.PPAIT2);
			CR040.setAIT3().move(PXCRTVAC.PPAIT3);
			CR040.setAIT4().move(PXCRTVAC.PPAIT4);
			CR040.setAIT5().move(PXCRTVAC.PPAIT5);
			CR040.setAIT6().move(PXCRTVAC.PPAIT6);
			CR040.setAIT7().move(PXCRTVAC.PPAIT7);
			CR040.setACAM(rateDifferenceAmount);
			CR040.setCUAM(0d);
			CR040.setVTCD(0);
			CR040.setVTAM(0d);
			CR040.setDBCR(' ');
			if (CRS750DS.getPBDCNY() == 1) {
				if (lessThan(CR040.getACAM(), cRefACAM.decimals(), 0d)) {
					CR040.setDBCR(CRS750DS.getPBDBNG());
				} else {
					CR040.setDBCR(CRS750DS.getPBDBPS());
				}
			}
			CR040.setDEDA(APIBH.getDEDA());
			ADAT04();
         CR040.setARAT(1d);
			CR040.setCVT1(0d);
			CR040.setCVT2(0d);
			CR040.setIVCL().moveLeftPad(invoiceClass);
			IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
			if (IN91) {
				XXTRNO++;
				CR040.setTRNO(XXTRNO);
				CR040.setDBCR(' ');
				if (CRS750DS.getPBDCNY() == 1) {
					if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
						CR040.setDBCR(CRS750DS.getPBDBNG());
					} else {
						CR040.setDBCR(CRS750DS.getPBDBPS());
					}
				}
				//    Entry method
				if (APIBH.getENME() == 1) {
					// Scanned invoice
					CR040.setENME(1);
				} else {
					CR040.setENME(0);
				}
				//   Accumulate for test of unbalanced voucher
				XTACAM += CR040.getACAM();
				CR040.setIVCL().moveLeftPad(invoiceClass); 	
				CR040.WRITE("10");
			} else {
				CR040.setACAM(CR040.getACAM() + rateDifferenceAmount);
				CR040.setDBCR(' ');
				if (CRS750DS.getPBDCNY() == 1) {
					if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
						CR040.setDBCR(CRS750DS.getPBDBNG());
					} else {
						CR040.setDBCR(CRS750DS.getPBDBPS());
					}
				}
				//   Accumulate for test of unbalanced voucher
				XTACAM += rateDifferenceAmount;
				CR040.UPDAT("10");
			}
      }
	}


   /**
   * Read payment tolerance
   */
   public void REPYTO() {
      GINPA.setCONO(APIBH.getCONO());
      GINPA.setDIVI().move(APIBH.getDIVI());
      GINPA.setINIO(toChar(4));
      GINPA.setOBV1().moveLeftPad(APIBH.getCUCD());
      GINPA.setOBV2().moveLeftPad(CR040.getSUCL());
      GINPA.setOBV3().moveLeftPad(APIBH.getSUNO());
      IN91 = !GINPA.CHAIN("00", GINPA.getKey("00", 6));
      if (IN91) {
         IN91 = !GINPA.CHAIN("00", GINPA.getKey("00", 5)); 
      }
      if (IN91) {
         IN91 = !GINPA.CHAIN("00", GINPA.getKey("00", 4)); 
      }
      if (!IN91) {
         XXTLPR = mvxHalfAdjust((double)XXPYTO * GINPA.getPDEI(), cRefCUAM.decimals());
         XXTLPR = mvxHalfAdjust((double)XXTLPR/100d, cRefCUAM.decimals());
         if (lessThan(GINPA.getPDEI(), cRefCUAM.decimals(), XXTLPR)) {
            XXTLPR = GINPA.getPDEI();
         }
         if (greaterThan(evaluateAmount, cRefCUAM.decimals(), XXTLPR)) {
            XXPYTO = 0d;
         }
      } else {
         XXPYTO = 0d;
      }
   }   

   /**
   * Create AP50-280 for unbalanced voucher
   */
   public void CR280() {
      //Cleare some fields in FCR040
      CLR04();
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      CR040.setTRCD(41);
      CR040.setAPRV(XXAPRV);
      //    Set params for CRTVACC (VAT accounting)
      PXCRTVAC.PPCONO = APIBH.getCONO();
      PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
      PXCRTVAC.PPEVEN.move(XXEVEN);
      PXCRTVAC.PPACTY.move("280 ");
      PXCRTVAC.PPFDAT = currentACDT;
      PXCRTVAC.PPCMTP = LDAZD.CMTP;
      PXCRTVAC.PPLANC.move(LDAZD.LANC);
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
      if (!APIBH.getSPYN().isBlank()) {
         PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
      } else {
         PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
      }
      //    Create accounting references
      IN92 = PXCRTVAC.CRTVACC();
      CR040.setAIT1().moveLeft(PXCRTVAC.PPAIT1); 	 	 	
      CR040.setAIT2().moveLeft(PXCRTVAC.PPAIT2); 	 	 	
      CR040.setAIT3().moveLeft(PXCRTVAC.PPAIT3); 	 	 	
      CR040.setAIT4().moveLeft(PXCRTVAC.PPAIT4); 	 	 	
      CR040.setAIT5().moveLeft(PXCRTVAC.PPAIT5); 	 	 	
      CR040.setAIT6().moveLeft(PXCRTVAC.PPAIT6); 	 	 	
      CR040.setAIT7().moveLeft(PXCRTVAC.PPAIT7); 	 	 	
      XXCUAM = 0d;
      XXACAM = -(XTACAM);
      CR040.setVTCD(0);
      XXVTAM = 0d;
      ADAT04();
      IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
      if (IN91) {
         XXTRNO++;
         CR040.setTRNO(XXTRNO);
         CR040.setCUAM(XXCUAM);
         CR040.setACAM(XXACAM);
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setVTAM(XXVTAM);
         } else {
            CR040.setVTAM(0d);
         }
         CR040.setDEDA(APIBH.getDEDA());
         CR040.setARAT(PLCLCCU.FZARAT);
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            }
         }
         //    Entry method
         if (APIBH.getENME() == 1) {
            // Scanned invoice
            CR040.setENME(1);
         } else {
            CR040.setENME(0);
         }
         CR040.setIVCL().moveLeftPad(invoiceClass); 	
         CR040.WRITE("10");
      } else {
         CR040.setCUAM(CR040.getCUAM() + XXCUAM);
         CR040.setACAM(CR040.getACAM() + XXACAM);
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) { 	
            CR040.setVTAM(XXVTAM);
         } else {
            CR040.setVTAM(0d);
         }
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            }
         }
         CR040.UPDAT("10");
      }
   }

   /**
   * accountLeftToDistrubute
   */
   public void accountLeftToDistrubute() {
      //Cleare some fields in FCR040
      CLR04();
      // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      CR040.setTRCD(41);
      CR040.setAPRV(XXAPRV);
      //    Set params for CRTVACC (VAT accounting)
      PXCRTVAC.PPCONO = APIBH.getCONO();
      PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
      PXCRTVAC.PPEVEN.move(XXEVEN);
      PXCRTVAC.PPACTY.move("280 ");
      PXCRTVAC.PPFDAT = currentACDT;
      PXCRTVAC.PPCMTP = LDAZD.CMTP;
      PXCRTVAC.PPLANC.move(LDAZD.LANC);
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
      if (!APIBH.getSPYN().isBlank()) {
         PXCRTVAC.PPSUNO.move(APIBH.getSPYN());
      } else {
         PXCRTVAC.PPSUNO.move(APIBH.getSUNO());
      } //·end if-else
      //    Create accounting references
      IN92 = PXCRTVAC.CRTVACC();
      CR040.setAIT1().move(PXCRTVAC.PPAIT1);
      CR040.setAIT2().move(PXCRTVAC.PPAIT2);
      CR040.setAIT3().move(PXCRTVAC.PPAIT3);
      CR040.setAIT4().move(PXCRTVAC.PPAIT4);
      CR040.setAIT5().move(PXCRTVAC.PPAIT5);
      CR040.setAIT6().move(PXCRTVAC.PPAIT6);
      CR040.setAIT7().move(PXCRTVAC.PPAIT7);
      if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
          APIBH.getDIVI().EQ(LDAZD.DIVI) ||
          APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
          APIBH.getDIVI().NE(LDAZD.DIVI)) {
         PLCLCCU.FZCUAM = X2CUAM;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZARAT = APIBH.getARAT();
         IN92 = PLCLCCU.CCLCCUR();
         X2ACAM = PLCLCCU.FZACAM;
      } else {
         X2ACAM = X2CUAM;
         PLCLCCU.FZARAT = APIBH.getARAT();
         PLCLCCU.FZRAFA = 0;
         PLCLCCU.FZCUAM = 0d;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZTEST = 0;
         PLCLCCU.FZDCAM = 0;
         PLCLCCU.FZLCDC = 0;
         PLCLCCU.FZTX15.clear();
         PLCLCCU.FZVERR = 0;
         PLCLCCU.FZMSGI.clear();
         PLCLCCU.FZMSGN = 0;
         PLCLCCU.FZMSGA.clear();
         IN92 = PLCLCCU.CCLCCUR();
      } //·end if-else
      CR040.setVTCD(0);
      XXVTAM = 0d;
      ADAT04();
      IN91 = !CR040.CHAIN_LOCK("10", CR040.getKey("10", 15));
      if (IN91) {
         XXTRNO++;
         CR040.setTRNO(XXTRNO);
         CR040.setCUAM(X2CUAM);
         CR040.setACAM(X2ACAM);
         if (LDAZD.TATM == 1) {
            CR040.setVTAM(XXVTAM);
         } else {
            CR040.setVTAM(0d);
         } //·end if-else
         CR040.setDEDA(APIBH.getDEDA());
         CR040.setARAT(PLCLCCU.FZARAT);
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            } //·end if-else
         } //·end if
         //    Entry method
         if (APIBH.getENME() == 1) {
            CR040.setENME(1);
         } else {
            CR040.setENME(0);
         } //·end if
         //   Accumulate for test of unbalanced voucher
         XTACAM += CR040.getACAM();
         XTCUAM += CR040.getCUAM();
         CR040.WRITE("10");
      } else {
         CR040.setCUAM(CR040.getCUAM() + X2CUAM);
         CR040.setACAM(CR040.getACAM() + X2ACAM);
         if (LDAZD.TATM == 1) {
            CR040.setVTAM(XXVTAM);
         } else {
            CR040.setVTAM(0d);
         } //·end if-else
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (lessThan(CR040.getCUAM(), cRefCUAM.decimals(), 0d)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            } //·end if-else
         } //·end if
         //   Accumulate for test of unbalanced voucher
         XTACAM += X2ACAM;
         XTCUAM += X2CUAM;
         CR040.UPDAT("10");
      } //·end if-else
      //   PRDE = 2 - Approved depending on status from invoice matching
      if (notApprovedForPayment_PRDE == 2) {
         //   -- Chain FGINHE --
         GINHE.setCONO(APIBH.getCONO());
         GINHE.setDIVI().move(APIBH.getDIVI());
         GINHE.setSUNO().move(APIBH.getSUNO());
         GINHE.setSINO().move(APIBH.getSINO());
         GINHE.setINYR(CR040.getINYR());
         IN91 = !GINHE.CHAIN("00", GINHE.getKey("00"));
         //   If invoice matching fully approved change to approved for payment
         if (!IN91) {
            DSINS0.move(GINHE.getINS0());
            if (DSINS1.getChar() == '3' &&
                DSINS2.getChar() == '3' &&
                DSINS3.getChar() == '3' &&
                DSINS4.getChar() == '3' &&
                DSINS5.getChar() == '3') {
               XXAPRV = 1;
               //   -- Read first record in FCR040 --
               CR040.setSTCF(0);
               CR040.setBCHN(currentBCHN);
               CR040.SETLL("00", CR040.getKey("00", 5));
               IN93 = !CR040.READE_LOCK("00", CR040.getKey("00", 5));
               while (!IN93) {
                  CR040.setAPRV(XXAPRV);
                  CR040.UPDAT("00");
                  //   -- Read next record in FCR040 --
                  IN93 = !CR040.READE_LOCK("00", CR040.getKey("00", 5));
               }
            }
         }
      }
   }

   /**
   * Set Accumulator Values
   */
   public void setAccumulatorValues() {
      X5IVNA = 0;
      // Read all invoice header charges
      GINHC.setCONO(APIBH.getCONO());
      GINHC.setDIVI().move(APIBH.getDIVI());
      GINHC.setSUNO().move(APIBH.getSUNO());
      GINHC.setSINO().move(APIBH.getSINO());
      GINHC.setINYR(CR040.getINYR());
      GINHC.setCDSE(0);
      GINHC.SETLL("00", GINHC.getKey("00", 6));
      IN93 = !GINHC.READE("00", GINHC.getKey("00", 5));
      while (!IN93) {
         if (GINHC.getIVSE() == 1) {
            X5IVNA += GINHC.getIEVA();
         }
         IN93 = !GINHC.READE("00", GINHC.getKey("00", 5));
      }
      // Read all Invoice lines
      GINLI.setCONO(APIBH.getCONO());
      GINLI.setDIVI().move(APIBH.getDIVI());
      GINLI.setSUNO().move(APIBH.getSUNO());
      GINLI.setSINO().move(APIBH.getSINO());
      GINLI.setINYR(CR040.getINYR());
      GINLI.SETLL("00", GINLI.getKey("00", 5));
      IN93 = !GINLI.READE("00", GINLI.getKey("00", 5));
      while (!IN93) {
         if (GINLI.getINLP() == 1 ||
             GINLI.getINLP() == 2 ||
             GINLI.getINLP() == 6) {
            X5IVNA += GINLI.getIVLC();
            X5IVNA += GINLI.getIVNA();
            X5IVNA += GINLI.getADDG();
         }
         IN93 = !GINLI.READE("00", GINLI.getKey("00", 5));
      }
   }

   /**
   * Cleare some fields in FCR040
   */
   public void CLR04() {
      CR040.setEXN1(0);
      CR040.setEXI1().clear();
      CR040.setEXN2(0);
      CR040.setEXI2().clear();
      CR040.setEXN3(0);
      CR040.setEXI3().clear();
      CR040.setEXN4(0);
      CR040.setEXI4().clear();
      CR040.setEXN5(0);
      CR040.setEXI5().clear();
      CR040.setAPCD().clear();
      CR040.setCVT1(0d); 	
      CR040.setCVT2(0d); 	
      CR040.setVTP1(0d); 	
      CR040.setVTP2(0d); 	
   }

  /**
   * Create Reconciled Payment 
   */
   public void createReconciledPayment() {
      boolean createPrepaymentTransactions = false;
      double summerizedACAM = 0d;
      double summerizedCUAM = 0d;
      if (isPrePaymentActivated()) {
         // Retrieve last used TRNO
         CR040.setTRNO(9999999);
         CR040.SETGT("50", CR040.getKey("50", 7));
         IN93 = !CR040.REDPE("50", CR040.getKey("50", 6));
         X2TRNO = CR040.getTRNO();
         createPrepaymentTransactions = false;
         // Read created records
         CR040.setTRNO(0);
         CR040.SETLL("00", CR040.getKey("00"));
         while (CR040.READE("00", CR040.getKey("00", 5))) {
            if (CR040.getTRCD() == 40) {
               //   Create 50-records for prepayment
               if (existExtraInfoBath == false) {
                  setExtraInfoBatch();
               }
               if (hasExtraInfoNumber(450) && hasExtraInfoNumber(451) && hasExtraInfoNumber(452)) {
                   createPrepaymentTransactions = true;                 
               }
               // Save actual transaction number for this 40-transaction
               actualTRNO = CR040.getTRNO();     
               // Create balance record for preinvoiced amount (TRCD=41)
               CR040.setTRNO(X2TRNO + 1);
               if (!CR040.CHAIN("00", CR040.getKey("00"))) {
                  CR040.setTRCD(41);
                  //    Set params for CRTVACC (VAT accounting)
                  PXCRTVAC.PPCONO = APIBH.getCONO();
                  PXCRTVAC.PPDIVI.move(APIBH.getDIVI());
                  PXCRTVAC.PPEVEN.move("AP53");               
                  PXCRTVAC.PPACTY.move("220 ");
                  PXCRTVAC.PPFDAT = CR040.getACDT();
                  PXCRTVAC.PPCMTP = LDAZD.CMTP;
                  PXCRTVAC.PPLANC.move(LDAZD.LANC);
                  PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
                  if (!CR040.getSPYN().isBlank()) {
                     PXCRTVAC.PPSUNO.move(CR040.getSPYN());
                  } else {
                     PXCRTVAC.PPSUNO.move(CR040.getSUNO());
                  }
                  PXCRTVAC.PPCUCD.move(CR040.getCUCD());
                  XVBSCD.moveLeft(CR040.getBSCD());
                  XVFTCO.moveLeft(CR040.getFTCO());
                  XVECAR.moveLeft(CR040.getECAR());
                  PXCRTVAC.PPCFI1.moveLeft(XVCFI1);
                  //    Create accounting references
                  IN92 = PXCRTVAC.CRTVACC();
                  CR040.setAIT1().move(PXCRTVAC.PPAIT1);
                  CR040.setAIT2().move(PXCRTVAC.PPAIT2);
                  CR040.setAIT3().move(PXCRTVAC.PPAIT3);
                  CR040.setAIT4().move(PXCRTVAC.PPAIT4);
                  CR040.setAIT5().move(PXCRTVAC.PPAIT5);
                  CR040.setAIT6().move(PXCRTVAC.PPAIT6);
                  CR040.setAIT7().move(PXCRTVAC.PPAIT7);
                  // Amount
                  CR040.setACAM(-(CR040.getACAM()));
                  CR040.setCUAM(-(CR040.getCUAM()));
                  CR040.setDBCR(' ');
                  if (CRS750DS.getPBDCNY() == 1) {
                     if (CR040.getCUAM() < (0d - EPS_2)) {
                        CR040.setDBCR(CRS750DS.getPBDBNG());
                     } else {
                        CR040.setDBCR(CRS750DS.getPBDBPS());
                     }
                  }
                  // Clear extra info
                  if (hasExtraInfoNumber(450)) {
                     deleteExtraInfo(450);
                  }
                  if (hasExtraInfoNumber(451)) {
                     deleteExtraInfo(451);
                  }
                  if (hasExtraInfoNumber(452)) {
                     deleteExtraInfo(452);
                  }
                  if (hasExtraInfoNumber(50)) {
                     deleteExtraInfo(50);
                  }
                  if (hasExtraInfoNumber(51)) {
                     deleteExtraInfo(51);
                  }
                  if (hasExtraInfoNumber(52)) {
                     deleteExtraInfo(52);
                  }
                  CR040.setRGTM(movexTime());
                  CR040.WRITE("00");
                  X2TRNO = CR040.getTRNO();
               }
            }
         }
         // Read all other transactions, except 40, that will update the above created record.
         summerizedACAM = 0d;
         summerizedCUAM = 0d;
         CR040.setTRNO(0);
         CR040.SETLL("00", CR040.getKey("00"));
         while (CR040.READE("00", CR040.getKey("00", 5))) {
            if (CR040.getTRNO() != X2TRNO) {
               // Amount
               summerizedACAM = summerizedACAM + (-(CR040.getACAM()));
               summerizedCUAM = summerizedCUAM + (-(CR040.getCUAM()));
            }
         }
         // Update balance record for preinvoiced amount (TRCD=41)
         CR040.setTRNO(X2TRNO);
         if (CR040.CHAIN_LOCK("00", CR040.getKey("00"))) {
            // Amount
            CR040.setACAM(summerizedACAM);
            CR040.setCUAM(summerizedCUAM);
            CR040.setDBCR(' ');
            if (CRS750DS.getPBDCNY() == 1) {
               if (CR040.getCUAM() < (0d - EPS_2)) {
                  CR040.setDBCR(CRS750DS.getPBDBNG());
               } else {
                  CR040.setDBCR(CRS750DS.getPBDBPS());
               }
            }
            CR040.setLMDT(movexDate());
            CR040.UPDAT("00");
         }
         //   Create 50-records for prepayment
         if (createPrepaymentTransactions) {
            createPrepaymentTransactions();                 
         }
      }
   }
   
  /**
   * Create prepayment transactions.
   *
   * We have to get the 40-transaction for both the prepayment and for the preinvoice, this to be able to create
   * payment transaction to each one.
   */
   public void createPrepaymentTransactions() {
      double currencyRatePI = 0d;
      int currencyDeviationTRNO = 0;
      currencyRatePR = 0d;
      // Save actual rate from trans 40
      currencyRatePI = CR040.getARAT();
      // Retrieve last used TRNO
      CR040.setTRNO(9999999);
      CR040.SETGT("50", CR040.getKey("50", 7));
      IN93 = !CR040.REDPE("50", CR040.getKey("50", 6));
      lastUsedTRNO = CR040.getTRNO();
          // Retrive actual transaction 40  
      CR040.setTRNO(actualTRNO);
      IN91 = !CR040.CHAIN("00", CR040.getKey("00"));
      // Save Extra Info 
      extraInfo450.moveLeftPad(getExtraInfoData(450));
      extraInfo451.moveLeftPad(getExtraInfoData(451));
      extraInfo452.moveLeftPad(getExtraInfoData(452));
      // Set part of key to FPLEDG
      PLEDG.setSINO().moveLeftPad(getExtraInfoData(451)); 
      alphaYEA4.moveLeftPad(getExtraInfoData(452));
      PLEDG.setINYR(alphaYEA4.getInt());  
      // Create payment transaction for the prepayment (TRCD=50)
      CR040.setTRNO(lastUsedTRNO + 1);
      if (!CR040.CHAIN_LOCK("00", CR040.getKey("00"))) {
         CR040.setTRCD(50);
         PLEDG.setCONO(LDAZD.CONO); 	
         PLEDG.setDIVI().move(CR040.getDIVI());
         if (!CR040.getSPYN().isBlank()) {
            PLEDG.setSPYN().moveLeftPad(CR040.getSPYN());
         } else {
            PLEDG.setSPYN().moveLeftPad(CR040.getSUNO());
         }                 
         PLEDG.setSUNO().moveLeftPad(CR040.getSUNO());
         PLEDG.setTRCD(40);                  	
         if (PLEDG.CHAIN("10", PLEDG.getKey("10", 7))) { 
            GLEDG.setCONO(PLEDG.getCONO()); 	
            GLEDG.setDIVI().move(PLEDG.getDIVI());
            GLEDG.setYEA4(PLEDG.getYEA4()); 
            GLEDG.setJRNO(PLEDG.getJRNO());
            GLEDG.setJSNO(PLEDG.getJSNO());                  	
            if (GLEDG.CHAIN("00", GLEDG.getKey("00"))) { 
               CR040.setAIT1().move(GLEDG.getAIT1());
               CR040.setAIT2().move(GLEDG.getAIT2());
               CR040.setAIT3().move(GLEDG.getAIT3());
               CR040.setAIT4().move(GLEDG.getAIT4());
               CR040.setAIT5().move(GLEDG.getAIT5());
               CR040.setAIT6().move(GLEDG.getAIT6());
               CR040.setAIT7().move(GLEDG.getAIT7());
            }
         } 
         CR040.setSINO().moveLeftPad(PLEDG.getSINO()); 
         CR040.setINYR(PLEDG.getINYR());
         // Amount
         CR040.setARAT(PLEDG.getARAT());
         CR040.setACAM(-(GLEDG.getACAM()));
         CR040.setCUAM(-(GLEDG.getCUAM()));
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (CR040.getCUAM() < (0d - EPS_2)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            }
         }
         if (!hasExtraInfoNumber(450) && !extraInfo450.isBlank()) {
            addExtraInfo(450, extraInfo450);
         }
         if (!hasExtraInfoNumber(451) && !extraInfo451.isBlank()) {
            addExtraInfo(451, extraInfo451);
         }
         if (!hasExtraInfoNumber(452) && !extraInfo452.isBlank()) {
            addExtraInfo(452, extraInfo452);
         }
         if (!hasExtraInfoNumber(50) && !extraInfo450.isBlank()) {
            addExtraInfo(50, extraInfo450);
         }
         if (!hasExtraInfoNumber(51) && !extraInfo451.isBlank()) {
            addExtraInfo(51, extraInfo451);
         }
         if (!hasExtraInfoNumber(52) && !extraInfo452.isBlank()) {
            addExtraInfo(52, extraInfo452);
         }
         CR040.setRGTM(movexTime());
         CR040.WRITE("00");
         lastUsedTRNO = CR040.getTRNO();
      } else {   
         CR040.UPDAT("00");
      }
      // Retrive actual transaction 40 
      CR040.setTRNO(actualTRNO);
      IN91 = !CR040.CHAIN("00", CR040.getKey("00"));
      // Set part of key to FPLEDG
      PLEDG.setSINO().moveLeftPad(getExtraInfoData(451)); 
      alphaYEA4.moveLeftPad(getExtraInfoData(452));
      PLEDG.setINYR(alphaYEA4.getInt());      
      // Create paymenttransaction for the preinvoice (TRCD=50)
      CR040.setTRNO(lastUsedTRNO + 1);
      if (!CR040.CHAIN("00", CR040.getKey("00"))) {
         CR040.setTRCD(50);
         currencyDeviationTRNO = CR040.getTRNO();
         // Set rest of key to FPLEDG
         PLEDG.setCONO(LDAZD.CONO); 	
         PLEDG.setDIVI().move(CR040.getDIVI());
         if (!CR040.getSPYN().isBlank()) {
            PLEDG.setSPYN().moveLeftPad(CR040.getSPYN());
         } else {
            PLEDG.setSPYN().moveLeftPad(CR040.getSUNO());
         }                
         PLEDG.setSUNO().moveLeftPad(CR040.getSUNO());         
         PLEDG.setTRCD(40);                   	
         if (PLEDG.CHAIN("10", PLEDG.getKey("10", 7))) { 
            currencyRatePR = PLEDG.getARAT();
            GLEDG.setCONO(PLEDG.getCONO()); 	
            GLEDG.setDIVI().move(PLEDG.getDIVI());
            GLEDG.setYEA4(PLEDG.getYEA4()); 
            GLEDG.setJRNO(PLEDG.getJRNO());
            GLEDG.setJSNO(PLEDG.getJSNO());                  	
            if (GLEDG.CHAIN("00", GLEDG.getKey("00"))) { 
               CR040.setAIT1().move(GLEDG.getAIT1());
               CR040.setAIT2().move(GLEDG.getAIT2());
               CR040.setAIT3().move(GLEDG.getAIT3());
               CR040.setAIT4().move(GLEDG.getAIT4());
               CR040.setAIT5().move(GLEDG.getAIT5());
               CR040.setAIT6().move(GLEDG.getAIT6());
               CR040.setAIT7().move(GLEDG.getAIT7());
            }
         }
         // Amount
         CR040.setACAM(-(CR040.getACAM()));
         CR040.setCUAM(-(CR040.getCUAM()));
         CR040.setDBCR(' ');
         if (CRS750DS.getPBDCNY() == 1) {
            if (CR040.getCUAM() < (0d - EPS_2)) {
               CR040.setDBCR(CRS750DS.getPBDBNG());
            } else {
               CR040.setDBCR(CRS750DS.getPBDBPS());
            }
         }
         if (!hasExtraInfoNumber(450) && !extraInfo450.isBlank()) {
            addExtraInfo(450, extraInfo450);
         }
         if (!hasExtraInfoNumber(451) && !extraInfo451.isBlank()) {
            addExtraInfo(451, extraInfo451);
         }
         if (!hasExtraInfoNumber(452) && !extraInfo452.isBlank()) {
            addExtraInfo(452, extraInfo452);
         }
         if (!hasExtraInfoNumber(50) && !extraInfo450.isBlank()) {
            addExtraInfo(50, extraInfo450);
         }
         if (!hasExtraInfoNumber(51) && !extraInfo451.isBlank()) {
            addExtraInfo(51, extraInfo451);
         }
         if (!hasExtraInfoNumber(52) && !extraInfo452.isBlank()) {
            addExtraInfo(52, extraInfo452);
         }
         CR040.setRGTM(movexTime());
         CR040.WRITE("00");
         X2TRNO = CR040.getTRNO();
      }
      // Check if currency Gain or Loss exists
      if (!equals(currencyRatePR, 6, currencyRatePI, 6) &&
          LDAZD.LOCD.NE(CR040.getCUCD())) {
         createPrepaymentTransactionsGainLoss(currencyDeviationTRNO);
      }
      // Retrive transaction 40 (reset)
      CR040.setTRNO(actualTRNO);
      IN91 = !CR040.CHAIN("00", CR040.getKey("00"));
      // Update (Block) actual reference number by changing status from 6 to 7
      updateFPPPAYWithNewStatus(7);
   }
   
  /**
   * Create prepayment transactions.
   *
   * We have to get the 40-transaction for both the prepayment and for the preinvoice, this to be able to create
   * payment transaction to each one.
   * @param TRNO  Transaction number
   */
   public void createPrepaymentTransactionsGainLoss(int TRNO) {
      // Retrive actual transaction 50 
      CR040.setTRNO(TRNO);
      IN91 = !CR040.CHAIN("00", CR040.getKey("00"));
      // Create deviation transaction for the preinvoice (TRCD=51)
      CR040.setTRNO(X2TRNO + 1);
      if (!CR040.CHAIN("00", CR040.getKey("00"))) {
         CR040.setTRCD(51);
         PLCLCCU.FZCONO = CR040.getCONO();
         PLCLCCU.FZDIVI.move(CR040.getDIVI());
         if (CR040.getDIVI().EQ(LDAZD.DIVI)) {
            PLCLCCU.FZDMCU = LDAZD.DMCU;
         } else {
            PLCLCCU.FZDMCU = MNDIV.getDMCU();
         }
         PLCLCCU.FZCUCD.move(CR040.getCUCD());
         PLCLCCU.FZCRTP = CR040.getCRTP();
         PLCLCCU.FZCUTD = CR040.getACDT();
         PLCLCCU.FZARAT = currencyRatePR;
         PLCLCCU.FZRAFA = 0;
         PLCLCCU.FZCUAM = CR040.getCUAM();
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZTEST = 0;
         PLCLCCU.FZCMTP = LDAZD.CMTP;
         PLCLCCU.FZDCAM = 0;
         PLCLCCU.FZLCDC = 0;
         PLCLCCU.FZTX15.clear();
         PLCLCCU.FZVERR = 0;
         PLCLCCU.FZMSGI.clear();
         PLCLCCU.FZMSGN = 0;
         PLCLCCU.FZMSGA.clear();
         IN92 = PLCLCCU.CCLCCUR();
         // Gain/Loss Amount = Amount PR - Amount PI
         CR040.setACAM(PLCLCCU.FZACAM - CR040.getACAM());
         if (!equals(CR040.getACAM(), 2, 0d)){
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            //    Set params for CRTVACC (curr diff +/-)
            PXCRTVAC.PPCONO = LDAZD.CONO;
            PXCRTVAC.PPDIVI.move(CR040.getDIVI());
            PXCRTVAC.PPADIV.move(CR040.getDIVI());
            PXCRTVAC.PPFDAT = CR040.getACDT();
            PXCRTVAC.PPCMTP = LDAZD.CMTP;
            PXCRTVAC.PPLANC.move(LDAZD.LANC);
            PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
            PXCRTVAC.PPEVEN.move("AP50");
            // Calculate Amount
            //CR040.setACAM((double) mvxHalfAdjust(CR040.getACAM() * (currencyRatePI-currencyRatePR), 2) );
            CR040.setCUAM(0d);
            //   - Set accountingtype depending on amount for gain/loss
            if (lessThan(CR040.getACAM(), 2, 0d)){
               PXCRTVAC.PPACTY.move("301 ");
            } else {
               PXCRTVAC.PPACTY.move("302 ");
            }
            if (!CR040.getSPYN().isBlank()) {
               PXCRTVAC.PPSUNO.move(CR040.getSPYN());
            } else {
               PXCRTVAC.PPSUNO.move(CR040.getSUNO());
            }
            PXCRTVAC.PPCUCD.move(CR040.getCUCD());
            //    Create accounting references
            IN92 = PXCRTVAC.CRTVACC();
            CR040.setAIT1().move(PXCRTVAC.PPAIT1);
            CR040.setAIT2().move(PXCRTVAC.PPAIT2);
            CR040.setAIT3().move(PXCRTVAC.PPAIT3);
            CR040.setAIT4().move(PXCRTVAC.PPAIT4);
            CR040.setAIT5().move(PXCRTVAC.PPAIT5);
            CR040.setAIT6().move(PXCRTVAC.PPAIT6);
            CR040.setAIT7().move(PXCRTVAC.PPAIT7);
            // Set debit/creditcode
            CR040.setDBCR(' ');
            if (CRS750DS.getPBDCNY() == 1) {
               if (lessThan(CR040.getACAM(), 2, 0d)){
                  CR040.setDBCR(CRS750DS.getPBDBNG());
               } else {
                  CR040.setDBCR(CRS750DS.getPBDBPS());
               }
            }
            CR040.setARAT(currencyRatePR);
            CR040.setVTCD(0);
            CR040.setVTP1(0d);
            CR040.setVTP2(0d);
            //  Save the actual payment rate in a extra info field on payment records
            savePaymentRate(currencyRatePR);
            //  Set other additional information belonging to this record
            if (!hasExtraInfoNumber(450) && !extraInfo450.isBlank()) {
               addExtraInfo(450, extraInfo450);
            }
            if (!hasExtraInfoNumber(451) && !extraInfo451.isBlank()) {
               addExtraInfo(451, extraInfo451);
            }
            if (!hasExtraInfoNumber(452) && !extraInfo452.isBlank()) {
               addExtraInfo(452, extraInfo452);
            }
            if (!hasExtraInfoNumber(50) && !extraInfo450.isBlank()) {
               addExtraInfo(50, extraInfo450);
            }
            if (!hasExtraInfoNumber(51) && !extraInfo451.isBlank()) {
               addExtraInfo(51, extraInfo451);
            }
            if (!hasExtraInfoNumber(52) && !extraInfo452.isBlank()) {
               addExtraInfo(52, extraInfo452);
            }
            CR040.setRGTM(movexTime());
            CR040.WRITE("00");
            XXTRNO = CR040.getTRNO();
            X2TRNO = CR040.getTRNO();  
         }
      }
   }
   
  /**
   * Save the actual payment rate in a extra info field on payment records   
   */
   public void savePaymentRate(double ARAT) {
      if (!equals(ARAT, 2, 0d)){
         this.PXDCCD = 6;
         this.PXFLDD = 5 + this.PXDCCD;
         this.PXEDTC = '4';
         this.PXDCFM = LDAZD.DCFM;
         this.PXNUM = ARAT;
         this.PXALPH.clear();      
         SRCOMNUM.COMNUM();
         ALPHA12.moveRight(this.PXALPH);   
         // 991 is a temporary info number will be removed in FCR040
         // by program CCRTCGL before calling GLS040      
         if (!hasExtraInfoNumber(991)) {
            addExtraInfo(991, RATE991);
         }
      }
   }
   
  /**
   * Retrieve Accounting With Payment Request Number 
   */
   public void retrieveAccountingWithPaymentRequestNumber() {
      PLEDG.setCONO(LDAZD.CONO); 	
      PLEDG.setDIVI().move(APIBH.getDIVI());
      if (!APIBH.getSPYN().isBlank()) {
         PLEDG.setSPYN().move(APIBH.getSPYN());
      } else {
         PLEDG.setSPYN().move(APIBH.getSUNO());
      }
      PLEDG.setSINO().moveLeftPad(APIBH.getPPYN());                 
      PLEDG.setSUNO().moveLeftPad(APIBH.getSUNO());
      PLEDG.setTRCD(40); 
      //PLEDG.setINYR(XXINYR.getInt()); 
      //XRDATE = APIBH.getIVDT();
      //RETYEA();
      //PLEDG.setINYR(XPYEA4);
      PLEDG.setINYR(APIBH.getYEA4());
      if (PLEDG.CHAIN("10", PLEDG.getKey("10", 7))) { 
         GLEDG.setCONO(PLEDG.getCONO()); 	
         GLEDG.setDIVI().move(PLEDG.getDIVI());
         GLEDG.setYEA4(PLEDG.getYEA4()); 
         GLEDG.setJRNO(PLEDG.getJRNO());
         GLEDG.setJSNO(PLEDG.getJSNO());                  	
         if (GLEDG.CHAIN("00", GLEDG.getKey("00"))) { 
            CR040.setAIT1().move(GLEDG.getAIT1());
            CR040.setAIT2().move(GLEDG.getAIT2());
            CR040.setAIT3().move(GLEDG.getAIT3());
            CR040.setAIT4().move(GLEDG.getAIT4());
            CR040.setAIT5().move(GLEDG.getAIT5());
            CR040.setAIT6().move(GLEDG.getAIT6());
            CR040.setAIT7().move(GLEDG.getAIT7());
         }
      } else {
         // Transaction not found
      }
   }
   
   /**
   *    retrieve Accounting Without Payment Request Number 
   */
   public void retrieveAccountingWithoutPaymentRequestNumber() {
      if  (APIBH.getIBTP().EQ(cRefIBTPext.PREPAYMENT_PREINVOICE())) {
         PXCRTVAC.PPEVEN.move("AP53");
      }
      if  (APIBH.getIBTP().EQ("06")) {
         PXCRTVAC.PPEVEN.move("AP54");
      }
      PXCRTVAC.PPACTY.move("210 ");
      PXCRTVAC.PPFDAT = currentACDT;
      PXCRTVAC.PPCMTP = LDAZD.CMTP;
      PXCRTVAC.PPLANC.move(LDAZD.LANC);
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
      //if (!APIBH.getSPYN().isBlank()) {
      //   PLEDG.setSPYN().move(APIBH.getSPYN());
      //} else {
      //   PLEDG.setSPYN().move(APIBH.getSUNO());
      //}
      XVECAR.move(XXECAR);
      XVFTCO.move(XXFTCO);
      XVBSCD.move(XXBSCD);
      PXCRTVAC.PPCFI1.moveLeft(XVCFI1);
      //    Create accounting references
      IN92 = PXCRTVAC.CRTVACC();
      CR040.setAIT1().move(PXCRTVAC.PPAIT1);
      CR040.setAIT2().move(PXCRTVAC.PPAIT2);
      CR040.setAIT3().move(PXCRTVAC.PPAIT3);
      CR040.setAIT4().move(PXCRTVAC.PPAIT4);
      CR040.setAIT5().move(PXCRTVAC.PPAIT5);
      CR040.setAIT6().move(PXCRTVAC.PPAIT6);
      CR040.setAIT7().move(PXCRTVAC.PPAIT7);
   }
   
  /**
   *    update Payment request from status 6 to 7
   */
   public boolean updateFPPPAYWithNewStatus(int newStatus) {
      if (CR040.getIVTP().EQ("PI") && APIBH.getYEA4() != 0) {	
         PPPAY.setCONO(CR040.getCONO());
         PPPAY.setDIVI().moveLeftPad(CR040.getDIVI());
         PPPAY.setPPYT().move("01");
         if (CR040.getSPYN().isBlank()) {
            PPPAY.setSPYN().moveLeftPad(CR040.getSUNO());
         } else {
            PPPAY.setSPYN().moveLeftPad(CR040.getSPYN());
         }
         PPPAY.setPPYR().moveLeftPad(getExtraInfoData(450));
         alphaYEA4.moveLeftPad(getExtraInfoData(452));
         PPPAY.setYEA4(alphaYEA4.getInt()); 
         PPPAY.setPPYN().moveLeftPad(getExtraInfoData(451));
         PPPAY.setSUNO().moveLeftPad(CR040.getSUNO());

         if (PPPAY.CHAIN_LOCK("00", PPPAY.getKey("00"))) {           
            if (PPPAY.getPPYS() == 6) {
               PPPAY.setPPYO(PPPAY.getPPYS());
               PPPAY.setPPYS(newStatus);
               PPPAY.setCHNO(PPPAY.getCHNO() + 1);
               PPPAY.setLMDT(this.CUDATE);
               PPPAY.setCHID().move(CR040.getCHID());
            }
            PPPAY.UPDAT("00");
         }
      }
      return true;
   }

  /**
   * Check for records in FCR04X.
   * Check if additional information exist for this job in FCR04X.
   * For higher performance we do that check only one time in the program.
   * We set the switch existExtraInfoBath true if the job have records in FCR04X.
   * If the switch existExtraInfoBath is false after this check, do not check anymore in FCR04X for more records.
   */
   public void setExtraInfoBatch() {
      // Do this only one time in the program,
      // thereafter you know if you need to call GLMNG04X anymore in the program
      existExtraInfoBath = false;
      existExtraInfoTrans = false;
      GLMNG04XDS.setGLMNG04XDS().clear();
      GLMNG04XDS.setF0OPER(GLMNG04X.OPER_BATCH_STATUS);
      //   - Set limit & build subfile
      GLMNG04XDS.setF0JBNO(CR040.getJBNO());
      GLMNG04XDS.setF0JBDT(CR040.getJBDT());
      GLMNG04XDS.setF0JBTM(CR040.getJBTM());
      GLMNG04XDS.setF0TRNB(0);
      GLMNG04XDS.setF0GEXN(0);
      GLMNG04XDS.setF0GEXI().clear();
      rMNG04XpreCall();
      apCall("GLMNG04X", rMNG04X);
      rMNG04XpostCall();
      if (GLMNG04XDS.getF0ERRF() == 1) {
      // Error: handle the error here.
      } else if (GLMNG04XDS.getF0NCLX() == 1) {
      // Record found: handle that here.
         existExtraInfoBath = true;
      } else {
         // Record NOT found: handle that here.
      }
   }
   
  /**
   * Returns true if the Extra Info number exist.
   * Check if the input value exist in some of the five element in FCR040, or in the file FCR04X.
   *
   * @param in_GEXN  Extra information number
   * @return True if the Extra Info Number was found.
   */
   public boolean hasExtraInfoNumber(int in_GEXN) {
      // Process first the five element in FCR040
      if (CR040.getEXN1() == in_GEXN) {
         return true;
      } else if (CR040.getEXN2() == in_GEXN) {
         return true;
      } else if (CR040.getEXN3() == in_GEXN) {
         return true;
      } else if (CR040.getEXN4() == in_GEXN) {
         return true;
      } else if (CR040.getEXN5() == in_GEXN) {
         return true;
      } else if (existExtraInfoBath) {
         // Process there after elements in FCR04X
         if (hasExtraInfoTrans()) {
            GLMNG04XDS.setF0OPER(GLMNG04X.OPER_GET);
            GLMNG04XDS.setF0JBNO(CR040.getJBNO());
            GLMNG04XDS.setF0JBDT(CR040.getJBDT());
            GLMNG04XDS.setF0JBTM(CR040.getJBTM());
            GLMNG04XDS.setF0BCHN(CR040.getBCHN());
            GLMNG04XDS.setF0TRNO(CR040.getTRNO());
            GLMNG04XDS.setF0GEXN(in_GEXN);
            GLMNG04XDS.setF0GEXI().clear();
            rMNG04XpreCall();
            apCall("GLMNG04X", rMNG04X);
            rMNG04XpostCall();
            if (GLMNG04XDS.getF0ERRF() == 1) {
            // Error: handle the error here.
            } else if (GLMNG04XDS.getF0NCLX() == 1) {
            // Record found: handle that here.
               return true;
            } else {
               // Record NOT found: handle that here.
            }
         }
      }
      return false;
   }
   
  /**
   * Deletes Extra Information Number from FCR040/FCR04X.
   * Seek the first Extra Information with the value of the in parameter.
   * If found in some of the five element in FCR040, clear both the Number and Information.
   * If found in FCR04X, delete that record.
   *
   * @param in_GEXN   Extra Information Number
   */
   public void deleteExtraInfo(int in_GEXN) {
      // Process first the five element in FCR040
      if (CR040.getEXN1() == in_GEXN) {
         CR040.setEXN1(0);
         CR040.setEXI1().clear();
      } else if (CR040.getEXN2() == in_GEXN) {
         CR040.setEXN2(0);
         CR040.setEXI2().clear();
      } else if (CR040.getEXN3() == in_GEXN) {
         CR040.setEXN3(0);
         CR040.setEXI3().clear();
      } else if (CR040.getEXN4() == in_GEXN) {
         CR040.setEXN4(0);
         CR040.setEXI4().clear();
      } else if (CR040.getEXN5() == in_GEXN) {
         CR040.setEXN5(0);
         CR040.setEXI5().clear();
      } else if (existExtraInfoBath) {
         // Process there after elements in FCR04X
         if (hasExtraInfoTrans()) {
            GLMNG04XDS.setF0NCLX(0);
            getExtraInfoData(in_GEXN);
            if (GLMNG04XDS.getF0NCLX() == 1 &&
                GLMNG04XDS.getF0ERRF() == 0) {
               GLMNG04XDS.setF0OPER(GLMNG04X.OPER_DELETE);
               rMNG04XpreCall();
               apCall("GLMNG04X", rMNG04X);
               rMNG04XpostCall();
            }
         }
      }
   }
   
  /**
   * Returns true if the current record in FCR040 have more Extra Information records in FCR04X.
   * For the current record in FCR040, read in FCR04X and check if there are connected records there.
   * For higher performance we do that check only one time for every record in FCR040.
   *
   * @return True if the current record in FCR040 have more Extra Information records in FCR04X.
   */
   public boolean hasExtraInfoTrans() {
      if (existExtraInfoBath &&
          (GLMNG04XDS.getF0BCHN() != CR040.getBCHN() ||
          GLMNG04XDS.getF0TRNO() != CR040.getTRNO())) {
         existExtraInfoTrans = false;
         GLMNG04XDS.setF0OPER(GLMNG04X.OPER_TRANS_STATUS);
         GLMNG04XDS.setF0JBNO(CR040.getJBNO());
         GLMNG04XDS.setF0JBDT(CR040.getJBDT());
         GLMNG04XDS.setF0JBTM(CR040.getJBTM());
         GLMNG04XDS.setF0BCHN(CR040.getBCHN());
         GLMNG04XDS.setF0TRNO(CR040.getTRNO());
         GLMNG04XDS.setF0TRNB(0);
         GLMNG04XDS.setF0GEXN(0);
         GLMNG04XDS.setF0GEXI().clear();
         rMNG04XpreCall();
         apCall("GLMNG04X", rMNG04X);
         rMNG04XpostCall();
         if (GLMNG04XDS.getF0ERRF() == 1) {
         // Error: handle the error here.
         } else if (GLMNG04XDS.getF0NCLX() == 1) {
         // Record found: handle that here.
            existExtraInfoTrans = true;
         } else {
            // Record NOT found: handle that here.
         }
      }
      return existExtraInfoTrans;
   }
   
  /**
   * Get data for a Information Number.
   * Find the first element with the Extra Information Number.
   * Return the found value.
   *
   * @param in_GEXN   Extra Information Number
   * @return MvxString with the data from the Extra Information Number.
   * If the Extra Information Number not was found, return blank.
   */
   public MvxString getExtraInfoData(int in_GEXN) {
      xOutGEXI.clear();
      // Process first the five element in FCR040
      if (CR040.getEXN1() == in_GEXN) {
         xOutGEXI.moveLeftPad(CR040.getEXI1());
      } else if (CR040.getEXN2() == in_GEXN) {
         xOutGEXI.moveLeftPad(CR040.getEXI2());
      } else if (CR040.getEXN3() == in_GEXN) {
         xOutGEXI.moveLeftPad(CR040.getEXI3());
      } else if (CR040.getEXN4() == in_GEXN) {
         xOutGEXI.moveLeftPad(CR040.getEXI4());
      } else if (CR040.getEXN5() == in_GEXN) {
         xOutGEXI.moveLeftPad(CR040.getEXI5());
      } else if (existExtraInfoBath) {
         // Process there after elements in FCR04X
         if (hasExtraInfoTrans()) {
            GLMNG04XDS.setF0OPER(GLMNG04X.OPER_GET);
            GLMNG04XDS.setF0JBNO(CR040.getJBNO());
            GLMNG04XDS.setF0JBDT(CR040.getJBDT());
            GLMNG04XDS.setF0JBTM(CR040.getJBTM());
            GLMNG04XDS.setF0BCHN(CR040.getBCHN());
            GLMNG04XDS.setF0TRNO(CR040.getTRNO());
            GLMNG04XDS.setF0GEXN(in_GEXN);
            GLMNG04XDS.setF0GEXI().clear();
            rMNG04XpreCall();
            apCall("GLMNG04X", rMNG04X);
            rMNG04XpostCall();
            if (GLMNG04XDS.getF0ERRF() == 1) {
            // Error: handle the error here.
            } else if (GLMNG04XDS.getF0NCLX() == 1) {
            // Record found: handle that here.
               xOutGEXI.moveLeftPad(GLMNG04XDS.getF0GEXI());
            } else {
               // Record NOT found: handle that here.
            }
         }
      }
      return xOutGEXI;
   }
   
  /**
   * Add Extra Information.
   * If found a empty place in some of the five element in FCR040, write this new Extra information there.
   * If all five element already is used in FCR04X, write the new Extra Information in FCR04X.
   *
   * @param in_GEXN   Extra Information number.
   * @param in_GEXI   Extra Information field.
   */
   public void addExtraInfo(int in_GEXN, MvxString in_GEXI) {
      if (CR040.getEXN1() == 0) {
         CR040.setEXN1(in_GEXN);
         CR040.setEXI1().moveLeftPad(in_GEXI);
      } else if (CR040.getEXN2() == 0) {
         CR040.setEXN2(in_GEXN);
         CR040.setEXI2().moveLeftPad(in_GEXI);
      } else if (CR040.getEXN3() == 0) {
         CR040.setEXN3(in_GEXN);
         CR040.setEXI3().moveLeftPad(in_GEXI);
      } else if (CR040.getEXN4() == 0) {
         CR040.setEXN4(in_GEXN);
         CR040.setEXI4().moveLeftPad(in_GEXI);
      } else if (CR040.getEXN5() == 0) {
         CR040.setEXN5(in_GEXN);
         CR040.setEXI5().moveLeftPad(in_GEXI);
      } else {
         GLMNG04XDS.setF0OPER(GLMNG04X.OPER_ADD);
         GLMNG04XDS.setF0JBNO(CR040.getJBNO());
         GLMNG04XDS.setF0JBDT(CR040.getJBDT());
         GLMNG04XDS.setF0JBTM(CR040.getJBTM());
         GLMNG04XDS.setF0BCHN(CR040.getBCHN());
         GLMNG04XDS.setF0TRNO(CR040.getTRNO());
         GLMNG04XDS.setF0GEXN(in_GEXN);
         GLMNG04XDS.setF0GEXI().moveLeftPad(in_GEXI);
         rMNG04XpreCall();
         apCall("GLMNG04X", rMNG04X);
         rMNG04XpostCall();
         if (GLMNG04XDS.getF0ERRF() == 1) {
         // Error: handle the error here.
         } else {
            existExtraInfoBath = true;
            existExtraInfoTrans = true;
         }
      }
   }   
   
  /**
   *    createR21 - Create VAT records for accounting type R21   
   */
   public void createR21() {
      // First a R21 with reversed sign
      XXTRNO++;
      CR040.setTRNO(XXTRNO);
      CR040.setACA()[0].move(PLCRTVT.FTAI21);
      CR040.setACA()[1].move(PLCRTVT.FTAI22);
      CR040.setACA()[2].move(PLCRTVT.FTAI23);
      CR040.setACA()[3].move(PLCRTVT.FTAI24);
      CR040.setACA()[4].move(PLCRTVT.FTAI25);
      CR040.setACA()[5].move(PLCRTVT.FTAI26);
      CR040.setACA()[6].move(PLCRTVT.FTAI27);
      CR040.setACTY().move(PLCRTVT.FTACT2);
      CR040.setAT04(PLCRTVT.FTA204);      
      CR040.setCUAM(-(XCVTA1));
      if (!isBlank(APIBL.getGLAM(), EPS_2)) {
         CR040.setACAM(-(APIBL.getGLAM()));
      } else {
         CR040.setACAM(-(XAVTA1));
      }
      CR040.setCVT1(0d);
      CR040.setCVT2(0d);      
      CR040.setVTAM(0d);
      CR040.setDEDA(APIBH.getDEDA());      
      CR040.setDBCR(' ');
      if (CRS750DS.getPBDCNY() == 1) {
         if (CR040.getCUAM() < (0d - EPS_2)) {
            CR040.setDBCR(CRS750DS.getPBDBNG());
         } else {
            CR040.setDBCR(CRS750DS.getPBDBPS());
         }
      }       	
      CR040.WRITE("10");
      // Second a R21 with same sign as accounting type 211
      XXTRNO++;
      CR040.setTRNO(XXTRNO);
      CR040.setACA()[0].move(PLCRTVT.FTAI31);
      CR040.setACA()[1].move(PLCRTVT.FTAI32);
      CR040.setACA()[2].move(PLCRTVT.FTAI33);
      CR040.setACA()[3].move(PLCRTVT.FTAI34);
      CR040.setACA()[4].move(PLCRTVT.FTAI35);
      CR040.setACA()[5].move(PLCRTVT.FTAI36);
      CR040.setACA()[6].move(PLCRTVT.FTAI37);
      CR040.setACTY().move(PLCRTVT.FTACT3);
      CR040.setAT04(PLCRTVT.FTA304);      
      CR040.setCUAM(XCVTA1);
      if (!isBlank(APIBL.getGLAM(), EPS_2)) {
         CR040.setACAM(APIBL.getGLAM());
      } else {
         CR040.setACAM(XAVTA1);
      }
      CR040.setCVT1(0d);
      CR040.setCVT2(0d);      
      CR040.setVTAM(0d);
      CR040.setDEDA(APIBH.getDEDA());      
      CR040.setDBCR(' ');
      if (CRS750DS.getPBDCNY() == 1) {
         if (CR040.getCUAM() < (0d - EPS_2)) {
            CR040.setDBCR(CRS750DS.getPBDBNG());
         } else {
            CR040.setDBCR(CRS750DS.getPBDBPS());
         }
      }       	
      CR040.WRITE("10");
   }
   
   /**   
   * Check if a specific program exist, indicating a market installation.
   *
   * @return false if the program does not exist.
   */
   public boolean checkMarket(String PGNM) {
      PXCHKOBJ.PXERR = '0';
      PXCHKOBJ.PXMSG = '1';
      PXCHKOBJ.PXLIB.moveLeft("*LIBL");
      PXCHKOBJ.PXOBJ.moveLeftPad(PGNM);
      PXCHKOBJ.PXTYPE.moveLeft("*PGM");
      this.PXMBR.moveLeft("*NONE");
      PXCHKOBJ.PXAUT.moveLeft("*NONE");
      IN92 = PXCHKOBJ.CCHKOBJ();
      if (PXCHKOBJ.PXERR == '1') {
         return false;
      }   
      return true;
   }
   
   /**
   * Update General Ledger
   */
   public void updateGeneralLedger() {
      // Get batch job number
      JBCMD.setBJNO().move(GLS040_BJNO);
      JBCMD.DELET("00", JBCMD.getKey("00", 1));
      // Prepare CJBCMD
      JBCMD.clearNOKEY("00");
      JBCMD.setCONO(APIBH.getCONO());
      JBCMD.setDIVI().clear();
      JBCMD.setJNA().move(this.DSJNA);
      JBCMD.setJNU(this.DSJNU.getInt());
      JBCMD.setLMDT(movexDate());
      JBCMD.setCHID().move(this.DSUSS);
      JBCMD.setCHNO(1);
      JBCMD.setRGDT(JBCMD.getLMDT());
      JBCMD.setRGTM(movexTime());
      // Select printer data
      // - Copy output definitions (CCTLSF, CSFOUT) for GLS041PF from 
      //   PXBJNO to GLS040_BJNO, then also copy to GLS041P4.
      paramQCMD.moveLeftPad("OVRPRTF FILE(GLS041PF  )");
      JBCMD.setFILE().moveLeftPad("GLS041P4");
      CROutput.outputDefs_copyAndCopy(/*fromBJNO*/ PXBJNO, /*toBJNO*/ GLS040_BJNO, 
         paramQCMD, /*toPRTF*/ JBCMD.getFILE(), pCCHGOVR);
      //   Select data
      GLS040DS.clear();
      GLS040DS.setZWUPCD(1);
      GLS040DS.setZWDLCD(1);
      GLS040DS.setZWPGNM().moveLeft("APS450");
      GLS040DS.setZWJRNO(0);
      GLS040DS.setZWSTCF(9);
      //   Line 99 - Save select data
      JBCMD.setBJLI().move("99");
      JBCMD.setBJLT().move("SLT");
      JBCMD.setFILE().clear();
      JBCMD.setQCMD().moveLeftPad(GLS040DS.get());
      JBCMD.setDATA().clear();
      JBCMD.WRITE("00");
      pGLS040CLpreCall();
      apCall("GLS040CL", pGLS040CL);
      pGLS040CLpostCall();
   }

   public int getPaymentClass(MvxString PYTP){ 	 	 	 	 	
      this.PXCONO = APIBH.getCONO(); 	 	 	 	 	
      this.PXDIVI.clear(); 	 	 	 	 	
      PXCHKTAB.PXSTCO.moveLeftPad("PYTP"); 	 	 	 	 	
      PXCHKTAB.PXSTKY.moveLeftPad(PYTP); 	 	 	 	 	
      this.PXLNCD.clear(); 	 	 	 	 	
      PXCHKTAB.PXCMTP = LDAZD.CMTP; 	 	 	 	 	
      PXCHKTAB.PXMUFF = 2; 	 	 	 	 	
      IN92 = PXCHKTAB.CCHKTAB(); 	 	 	 	 	
      if (PXCHKTAB.PXPEER == 0) { 	 	 	 	 	
         DSPYTP.setDSPYTP().moveLeft(PXCHKTAB.PXPARM); 	 	 	 	 	
      } else { 	
         DSPYTP.setDSPYTP().clear(); 	
      } 	
      return DSPYTP.getFWPYCL(); 	 	 	 	 	
   } 	

  /**
   * checkPreviousCorrective - Check if any previous corrective invoices
   *                           exist for an original invoice <br>
   */
   public void checkPreviousCorrective() { 	
      previousCorr.clear(); 	
      previousInyr = 0; 	
      PLEDX.setCONO(CR040.getCONO()); 	
      PLEDX.setDIVI().move(CR040.getDIVI()); 	
      PLEDX.setYEA4(original_YEA4); 	
      PLEDX.setJRNO(original_JRNO); 	
      PLEDX.setJSNO(original_JSNO); 	
      PLEDX.setPEXN(436); 	
      IN91 = !PLEDX.CHAIN("00", PLEDX.getKey("00", 6)); 	
      if (!IN91) { 	
         XXInvInfo.moveLeft(PLEDX.getPEXI()); 	
         previousCorr.moveLeft(XXInvoice); 	
         previousInyr = XXInvYear.getInt(); 	
      } 	
   } 	
   
   /**
   * updateOriginalInvoice - Update Original invoice with the actual
   *                         corrective invoice <br>
   */
   public void updateOriginalInvoice() { 	
      PLEDX.setCONO(CR040.getCONO()); 	
      PLEDX.setDIVI().move(CR040.getDIVI()); 	
      PLEDX.setYEA4(original_YEA4); 	
      PLEDX.setJRNO(original_JRNO); 	
      PLEDX.setJSNO(original_JSNO); 	
      PLEDX.setPEXN(436); 	
      XXInvoice.moveLeft(CR040.getSINO()); 	
      XXInvYear.move(CR040.getINYR()); 	
      PLEDX.setPEXI().moveLeft(XXInvInfo); 	
      PLEDX.setPEXS(1); 	
      IN91 = !PLEDX.CHAIN("00", PLEDX.getKey("00")); 	
      if (IN91) { 	
         PLEDX.setRGDT(movexDate()); 	
         PLEDX.setRGTM(movexTime()); 	
         PLEDX.setLMDT(PLEDX.getRGDT()); 	
         PLEDX.setCHNO(1); 	
         PLEDX.setCHID().move(this.DSUSS); 	
         PLEDX.WRITE("00"); 	
      } 	
   } 	
 	
  /**
   * Check and send From/To country as input to CCRTVAT 
   */
   public void sendFromToCountryAsInput() {
      // If VAT override and not EU and not reverse charge of VAT    
      // and different country codes, send From/To country as input to CCRTVAT 
      if (DSVTCD.getYKVTOV() == 1 && DSVTCD.getYKVATT() != 8  &&    
          XXEUVT == 0 && XXBSCD.NE(XXFTCO)) {  
         PLCRTVT.FTCSCD.move(XXFTCO);  
         PLCRTVT.FTECAR.clear();    
      }
   }
   
  /**
   * Gets Division data
   *
   * @param DIVI Division
   */
   public void getCMNDIV(MvxString DIVI) {
      if (CMNDIV_CONO != APIBH.getCONO() ||
          CMNDIV_DIVI.NE(DIVI)) {
         CMNDIV_CONO = APIBH.getCONO();
         CMNDIV_DIVI.moveLeftPad(DIVI);
         XXDMCU = LDAZD.DMCU;
         XXLOCD.move(LDAZD.LOCD);
         XXLCDC = LDAZD.LCDC;
         XXPTFA = LDAZD.PTFA;
         found_CMNDIV = cRefDIVIext.getCMNDIV(MNDIV, found_CMNDIV, APIBH.getCONO(), DIVI);
         if (found_CMNDIV) {
            XXPTFA = MNDIV.getPTFA(); 	
            XXDMCU = MNDIV.getDMCU();
            XXLOCD.move(MNDIV.getLOCD());
            found_CSYTAB_CUCD = cRefCUCDext.getCSYTAB_CUCD(SYTAB, found_CSYTAB_CUCD, APIBH.getCONO(), MNDIV.getLOCD());
            if (found_CSYTAB_CUCD) {
               cRefCUCDext.setDSCUCD(SYTAB, DSCUCD);
               XXLCDC = DSCUCD.getYQDCCD();
            }
         }
      }
   }

   /**
   * Gets CRS750DS - Parameters for GL entry
   * @param DIVI division
   * @return true if CRS750DS was found.
   */
   public boolean getCRS750DS(MvxString DIVI) {
      // Do not read if same values as last time
      if (APIBH.getCONO() != CRS750DS_CONO ||
          DIVI.NE(CRS750DS_DIVI)) {
         CRS750DS_CONO = APIBH.getCONO();
         CRS750DS_DIVI.moveLeftPad(DIVI);
         SYPAR.setCONO(APIBH.getCONO());
         SYPAR.setDIVI().moveLeftPad(DIVI);
         SYPAR.setSTCO().moveLeftPad("CRS750");
         found_CRS750DS = SYPAR.CHAIN("00", SYPAR.getKey("00"));
         //   If not found and MUC, try division blank (central)
         if (IN91 && LDAZD.CMTP == 2 &&
            !SYPAR.getDIVI().isBlank()) {
            SYPAR.setDIVI().clear();
            found_CRS750DS = SYPAR.CHAIN("00", SYPAR.getKey("00"));
         }
         if (found_CRS750DS) {
            CRS750DS.setCRS750DS().moveLeft(SYPAR.getPARM());
         } else {
            CRS750DS.setCRS750DS().clear();
         }
      }
      return found_CRS750DS;
   }

   /**
   * Returns true if Invoice batch number status could be set to - Printing in progress
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   *
   * @return true if Invoice batch number status could be set to - Printing in progress 
   */
   public boolean lockInvoiceForPrint(MvxString DIVI, long INBN) {
      // =======================================================
      // Set Invoice batch number to status - Printing in progress
      // =======================================================
      pAPS450Fnc_lockForPrint = get_pAPS450Fnc_lockForPrint();
      pAPS450Fnc_lockForPrint.messages.forgetNotifications();
      pAPS450Fnc_lockForPrint.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_lockForPrint.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_lockForPrint.INBN.set(INBN);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_lockForPrint);
      LDAZZ.FPNM.move(this.DSPGM);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_lockForPrint.messages.existError()) {
         // Release resources allocated by the parameter list.
         pAPS450Fnc_lockForPrint.release();
         // Return error messages
         return false;
      }
      // Release resources allocated by the parameter list.
      pAPS450Fnc_lockForPrint.release();
      return true;
   }

   /**
   * Returns true if invoice can be locked for update (Set Invoice batch number to status - Update in progress)
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   * @param messages
   *    Container with list of messages
   *
   * @return true if invoice can be locked for update
   */
   public boolean lockInvoiceForUpdAPL(MvxString DIVI, long INBN) {
      // ===========================================================
      // Set Invoice batch number to status - Update to APL progress
      // ===========================================================
      pAPS450Fnc_lockForUpdAPL = get_pAPS450Fnc_lockForUpdAPL();
      pAPS450Fnc_lockForUpdAPL.messages.forgetNotifications();
      pAPS450Fnc_lockForUpdAPL.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_lockForUpdAPL.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_lockForUpdAPL.INBN.set(INBN);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_lockForUpdAPL);
      LDAZZ.FPNM.move(this.DSPGM);
      // =========================================
      // Handle messages
      if (pAPS450Fnc_lockForUpdAPL.messages.existError()) {
         // Release resources allocated by the parameter list.
         pAPS450Fnc_lockForUpdAPL.release();
         return false;
      }
      // Release resources allocated by the parameter list.
      pAPS450Fnc_lockForUpdAPL.release();
      return true;
   }

   /**
   * Sets Invoice batch number to status - Unlocked
   * @param DIVI
   *    Division
   * @param INBN
   *    Supplier invoice batch number
   */
   public void unlockInvoice(MvxString DIVI, long INBN) {
      // =============================================
      // Set Invoice batch number to status - Unlocked
      // =============================================
      pAPS450Fnc_unlock = get_pAPS450Fnc_unlock();
      pAPS450Fnc_unlock.messages.forgetNotifications();
      pAPS450Fnc_unlock.prepare();
      // Set input parameters
      // - Division
      pAPS450Fnc_unlock.DIVI.set().moveLeftPad(DIVI);
      // - Invoice batch number
      pAPS450Fnc_unlock.INBN.set(INBN);
      // =========================================
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_unlock);
      LDAZZ.FPNM.move(this.DSPGM);
      // =========================================
      // Release resources allocated by the parameter list.
      pAPS450Fnc_unlock.release();
   }

   public boolean useTaxFound() {
      if (MNDIV.getTATM() == 2) { 
         if (APIBH.getTXIN() == 0) {
            // read FGINLC for this invoice to find
            // any use tax records
            GINLC.setCONO(APIBH.getCONO());
            GINLC.setDIVI().move(APIBH.getDIVI());
            GINLC.setSUNO().move(APIBH.getSUNO());
            GINLC.setSINO().move(APIBH.getSINO());
            GINLC.setINYR(CR040.getINYR());
            GINLC.SETLL("00", GINLI.getKey("00", 5));
            IN93 = !GINLC.READE("00", GINLI.getKey("00", 5));
            while (!IN93) {
               if (!(equals(GINLC.getIVNA(), 0d, 6))) { 	
                  // read MPCELE
                  PCELE.setCONO(GINLC.getCONO());
                  PCELE.setCEID().moveLeftPad(GINLC.getCEID());
                  if (PCELE.getWSOP().EQ("90")) {
                     return true;
                  }
                  IN93 = !GINLC.READE("00", GINLI.getKey("00", 5));
               }   
            }
         }
      }
      return false;
   }

   /**
   *   insertExtraInfo - Insert extra info to be written later in FGLEDX
   *   
   *   @param infoNum -  extra info number
   *   @param infoString -  extra info string
   *   @return isSuccessful - successfully inserted GL extra info or not
   */
   public boolean insertExtraInfo(int infoNum, MvxString infoString) {  
      boolean isSuccessful = false;    
      //   Clear FCR040 first from the same info num     
      clearExtraInfo(infoNum);   
      if (CR040.getEXN1() == 0 && CR040.getEXI1().isBlank()) {    
         CR040.setEXN1(infoNum);    
         CR040.setEXI1().moveLeftPad(infoString);  
         isSuccessful = true;    
      } else if (CR040.getEXN2() == 0 && CR040.getEXI2().isBlank()) {   
         CR040.setEXN2(infoNum);    
         CR040.setEXI2().moveLeftPad(infoString);     
         isSuccessful = true;    
      } else if (CR040.getEXN3() == 0 && CR040.getEXI3().isBlank()) {   
         CR040.setEXN3(infoNum);    
         CR040.setEXI3().moveLeftPad(infoString);  
         isSuccessful = true;    
      } else if (CR040.getEXN4() == 0 && CR040.getEXI4().isBlank()) {   
         CR040.setEXN4(infoNum);    
         CR040.setEXI4().moveLeftPad(infoString);  
         isSuccessful = true;    
      } else if (CR040.getEXN5() == 0 && CR040.getEXI5().isBlank()) {   
         CR040.setEXN5(infoNum);    
         CR040.setEXI5().moveLeftPad(infoString);  
         isSuccessful = true;    
      }  
      return isSuccessful;    
   }     
   
   /**
   *   clearExtraInfo - clear FCR040 from a specified extra info number
   *   
   *   @param infoNum Extra info number
   */
   public void clearExtraInfo(int infoNum) {    
      if (CR040.getEXN1() == infoNum){    
         CR040.setEXN1(0);    
         CR040.setEXI1().clear();   
      }  
      if (CR040.getEXN2() == infoNum) {   
         CR040.setEXN2(0);    
         CR040.setEXI2().clear();   
      }  
      if (CR040.getEXN3() == infoNum) {   
         CR040.setEXN3(0);    
         CR040.setEXI3().clear();   
      }  
      if (CR040.getEXN4() == infoNum) {   
         CR040.setEXN4(0);    
         CR040.setEXI4().clear();   
      }  
      if (CR040.getEXN5() == infoNum) {   
         CR040.setEXN5(0);    
         CR040.setEXI5().clear();   
      }  
   }  
    
   /**
   *   hasSupplierVRNO - check and retrieve Vat Reg No from supplier
   *                     special handling for TRCD 41 and 51
   *   
   *   @return true if VRNO is found for supplier
   */
   public boolean hasSupplierVRNO() {  
      supVNROexists = false;  
      supVRNO.clear();  
      if (CR040.getTRCD() == 41 || CR040.getTRCD() == 51) {    
         if (CR040.getTRCD() == 51 && !CR040.getVRNO().isBlank()) {  
            supVRNO.moveLeftPad(CR040.getVRNO());  
            supVNROexists = true;   
         } else {    
            IDMAS.setCONO(CR040.getCONO());  
            IDMAS.setSUNO().move(CR040.getSUNO());    
            if (IDMAS.CHAIN("00", IDMAS.getKey("00"))) {    
               supVRNO.moveLeftPad(IDMAS.getVRNO());  
               supVNROexists = true;   
            }  
         }  
      }  
      return supVNROexists;   
   }   
   
   /**
   *  createSelfAssessedTax - Create a Self assessed Tax record
   *                          in FCR040 
   */
   public void createSelfAssessedTax() {
      XAVTA3 = 0d;   
      XCVTA3 = 0d;
      PLCRTVT.FTCONO = LDAZD.CONO;
      PLCRTVT.FTDIVI.move(APIBL.getDIVI());
      PLCRTVT.FTCMTP = LDAZD.CMTP;
      PLCRTVT.FTTASK = 3;      
      PLCRTVT.FTVTCD = XXVTCD;
      PLCRTVT.FTACDT = APS455DS.getZWACDT();
      PLCRTVT.FTLCDC = X1LCDC;
      PLCRTVT.FTBAAM = XXCUAM;
      // Net   
      PLCRTVT.FTVATH = 1;       
      PLCRTVT.FTCUCD.move(APIBH.getCUCD());
      PLCRTVT.FTEVEN.move(XXEVEN);
      PLCRTVT.FTTECD.move(APIBH.getTECD());
      if (!APIBH.getSPYN().isBlank()) {
         PLCRTVT.FTSUNO.move(APIBH.getSPYN());
      } else {
         PLCRTVT.FTSUNO.move(APIBH.getSUNO());
      }
      PLCRTVT.FTCUNO.clear();
      PLCRTVT.FTCSCD.move(XXBSCD);
      PLCRTVT.FTECAR.move(XXECAR);
      if (XXEUVT == 1) {
         PLCRTVT.FTIOCD = 3;
      } else {
         PLCRTVT.FTIOCD = 2;
         // If VAT override and not EU and not reverse charge of VAT
         // and different country codes, send From/To country as input to CCRTVAT
         if (DSVTCD.getYKVTOV() == 1 && DSVTCD.getYKVATT() != 8  && 
             XXEUVT == 0 && XXBSCD.NE(XXFTCO)) {
            PLCRTVT.FTCSCD.move(XXFTCO);
            PLCRTVT.FTECAR.clear();
         }
      }
      PLCRTVT.FTAI11.clear();
      XVECAR.move(XXECAR);
      XVFTCO.move(XXFTCO);
      XVBSCD.move(XXBSCD);
      PLCRTVT.FTAI11.moveLeft(XVCFI1);
      PLCRTVT.CCRTVAT();
      if (!isBlank(PLCRTVT.FTVTM2, EPS_2)) {
         // - Fetch amount in local currency
         if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
             APIBH.getDIVI().EQ(LDAZD.DIVI) ||
             APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
             APIBH.getDIVI().NE(LDAZD.DIVI)) {
            PLCLCCU.FZCUAM = PLCRTVT.FTVTM3;
            PLCLCCU.FZACAM = 0d;
            PLCLCCU.FZARAT = APIBH.getARAT();
            PLCLCCU.CCLCCUR();
            XAVTA2 = PLCLCCU.FZACAM;
         } else {
            XAVTA2 = PLCRTVT.FTVTM2;
            PLCLCCU.FZARAT = APIBH.getARAT();
            PLCLCCU.FZRAFA = 0;
            PLCLCCU.FZCUAM = 0d;
            PLCLCCU.FZACAM = 0d;
            PLCLCCU.FZTEST = 0;
            PLCLCCU.FZDCAM = 0;
            PLCLCCU.FZLCDC = 0;
            PLCLCCU.FZTX15.clear();
            PLCLCCU.FZVERR = 0;
            PLCLCCU.FZMSGI.clear();
            PLCLCCU.FZMSGN = 0;
            PLCLCCU.FZMSGA.clear();
            IN92 = PLCLCCU.CCLCCUR();
         }
         XCVTA2 = PLCRTVT.FTVTM2;
      }      
      CR040.setVTP1(0d);          
      CR040.setVTP2(PLCRTVT.FTVTP2);         
      CR040.setVTAM(0d);         
      XXVTAM = 0d;      
      CR040.setACA()[0].move(PLCRTVT.FTAI21);         
      CR040.setACA()[1].move(PLCRTVT.FTAI22);         
      CR040.setACA()[2].move(PLCRTVT.FTAI23);   
      CR040.setACA()[3].move(PLCRTVT.FTAI24);         
      CR040.setACA()[4].move(PLCRTVT.FTAI25);      
      CR040.setACA()[5].move(PLCRTVT.FTAI26);         
      CR040.setACA()[6].move(PLCRTVT.FTAI27);                        
      ADAT04();         
      IN91 = !CR040.CHAIN("10", CR040.getKey("10", 15));        
      if (IN91) {          
         CR040.setCUAM(XCVTA2);        
         CR040.setACAM(XAVTA2);        
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) {    
            CR040.setVTAM(XXVTAM);        
         } else {          
            CR040.setVTAM(0d);         
         }        
         CR040.setCVT1(0d);         
         CR040.setCVT2(0d);         
         CR040.setDEDA(APIBH.getDEDA());        
         CR040.setARAT(PLCLCCU.FZARAT);         
         XXTRNO++;         
         CR040.setTRNO(XXTRNO);        
         CR040.setDBCR(' ');        
         if (CRS750DS.getPBDCNY() == 1) {          
            if (CR040.getCUAM() < (0d - EPS_2)) {        
               CR040.setDBCR(CRS750DS.getPBDBNG());   
            } else {          
               CR040.setDBCR(CRS750DS.getPBDBPS());         
            }        
         }     
         //    Entry method
         if (APIBH.getENME() == 1) {
            // Scanned invoice         
            CR040.setENME(1);
         } else {
            CR040.setENME(0);
         }     
         CR040.setIVCL().moveLeftPad(invoiceClass);
         // Update for GL Extra info 89 Self assessed tax rate                             
         // Clear extra info
         if (hasExtraInfoNumber(89)) {
            deleteExtraInfo(89);
         }    
         // Convert Self assessd  rate
         this.PXALPH.clear();
         if (!isBlank(PLCRTVT.FTVTP2, EPS_2)) {
            this.PXCONO = CR040.getCONO();
            this.PXDIVI.clear();
            this.PXDCCD = 2;
            this.PXFLDD = 3 + this.PXDCCD;
            this.PXEDTC = 'L';
            this.PXDCFM = LDAZD.DCFM;
            this.PXNUM = VATPC.getVTP1();
            SRCOMNUM.PXDBCR = ' ';
            SRCOMNUM.PXDCYN = 0;
            SRCOMNUM.COMNUM();
         }
         alpha7.moveRight(this.PXALPH);
         //   Update for GL Extra info 89 Self assessed tax rate
         if (!hasExtraInfoNumber(89)) {
            addExtraInfo(89, alpha7);
         }
         CR040.setACTY().moveLeft("212"); 
         if (DSFFNC.getDFAVAT() != 4) {
            CR040.WRITE("10");             
         }
         // Accumulate for test of unbalanced voucher
         XTACAM += XAVTA2;
         XTCUAM += XCVTA2;
         
         if (hasExtraInfoNumber(89)) {
            deleteExtraInfo(89);
         }
         CR040.setACTY().clear();
      }
      if (!isBlank(PLCRTVT.FTVTM3, EPS_2)) {
         // - Fetch amount in local currency
         if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
             APIBH.getDIVI().EQ(LDAZD.DIVI) ||
             APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
             APIBH.getDIVI().NE(LDAZD.DIVI)) {
            PLCLCCU.FZCUAM = PLCRTVT.FTVTM3;
            PLCLCCU.FZACAM = 0d;
            PLCLCCU.FZARAT = APIBH.getARAT();
            PLCLCCU.CCLCCUR();
            XAVTA3 = PLCLCCU.FZACAM;
         } else {
            XAVTA3 = PLCRTVT.FTVTM3;
            PLCLCCU.FZARAT = APIBH.getARAT();
            PLCLCCU.FZRAFA = 0;
            PLCLCCU.FZCUAM = 0d;
            PLCLCCU.FZACAM = 0d;
            PLCLCCU.FZTEST = 0;
            PLCLCCU.FZDCAM = 0;
            PLCLCCU.FZLCDC = 0;
            PLCLCCU.FZTX15.clear();
            PLCLCCU.FZVERR = 0;
            PLCLCCU.FZMSGI.clear();
            PLCLCCU.FZMSGN = 0;
            PLCLCCU.FZMSGA.clear();
            IN92 = PLCLCCU.CCLCCUR();
         }
         XCVTA3 = PLCRTVT.FTVTM3;
      }      
      CR040.setVTP1(0);          
      CR040.setVTP2(PLCRTVT.FTVTP2);         
      CR040.setVTAM(0d);         
      XXVTAM = 0d;      
      CR040.setACA()[0].move(PLCRTVT.FTAI31);         
      CR040.setACA()[1].move(PLCRTVT.FTAI32);         
      CR040.setACA()[2].move(PLCRTVT.FTAI33);   
      CR040.setACA()[3].move(PLCRTVT.FTAI34);         
      CR040.setACA()[4].move(PLCRTVT.FTAI35);      
      CR040.setACA()[5].move(PLCRTVT.FTAI36);         
      CR040.setACA()[6].move(PLCRTVT.FTAI37);                        
      ADAT04();         
      IN91 = !CR040.CHAIN("10", CR040.getKey("10", 15));        
      if (IN91) {          
         CR040.setCUAM(XCVTA3);        
         CR040.setACAM(XAVTA3);        
         if (LDAZD.TATM == 1 || LDAZD.TATM == 4) {    
            CR040.setVTAM(XXVTAM);        
         } else {          
            CR040.setVTAM(0d);         
         }        
         CR040.setCVT1(0d);         
         CR040.setCVT2(0d);         
         CR040.setDEDA(APIBH.getDEDA());        
         CR040.setARAT(PLCLCCU.FZARAT);         
         XXTRNO++;         
         CR040.setTRNO(XXTRNO);        
         CR040.setDBCR(' ');        
         if (CRS750DS.getPBDCNY() == 1) {          
            if (CR040.getCUAM() < (0d - EPS_2)) {        
               CR040.setDBCR(CRS750DS.getPBDBNG());   
            } else {          
               CR040.setDBCR(CRS750DS.getPBDBPS());         
            }        
         }     
         //    Entry method
         if (APIBH.getENME() == 1) {
            // Scanned invoice          
            CR040.setENME(1);
         } else {
            CR040.setENME(0);
         }     
         CR040.setIVCL().moveLeftPad(invoiceClass);
         // Update for GL Extra info 89 Self assessed tax rate                             
         // Clear extra info
         if (hasExtraInfoNumber(89)) {
            deleteExtraInfo(89);
         }    
         // Convert Self assessd  rate
         this.PXALPH.clear();
         if (!isBlank(PLCRTVT.FTVTP2, EPS_2)) {
            this.PXCONO = CR040.getCONO();
            this.PXDIVI.clear();
            this.PXDCCD = 2;
            this.PXFLDD = 3 + this.PXDCCD;
            this.PXEDTC = 'L';
            this.PXDCFM = LDAZD.DCFM;
            this.PXNUM = VATPC.getVTP2();
            SRCOMNUM.PXDBCR = ' ';
            SRCOMNUM.PXDCYN = 0;
            SRCOMNUM.COMNUM();
         }
         alpha7.moveRight(this.PXALPH);
         //   Update for GL Extra info 89 Self assessed tax rate
         if (!hasExtraInfoNumber(89)) {
            addExtraInfo(89, alpha7);
         }
         CR040.setACTY().moveLeft("217"); 
         if (DSFFNC.getDFAVAT() != 4) {
            CR040.WRITE("10");             
         }
         // Accumulate for test of unbalanced voucher
         XTACAM += XAVTA3;
         XTCUAM += XCVTA3;
         
         if (hasExtraInfoNumber(89)) {
            deleteExtraInfo(89);
         }
         CR040.setACTY().clear();
      }               
   }
   
   /**
    *    createDeductibleVAT_1 - Create deductible VAT record in FCR040
    */
    public void createDeductibleVAT_1() {                           
       XXCDED = -(XDVTMX);
       //    Convert - Currency --> Local currency
       if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
           APIBH.getDIVI().EQ(LDAZD.DIVI) ||
           APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
           APIBH.getDIVI().NE(LDAZD.DIVI)) {
          PLCLCCU.FZCUAM = XXCDED;
          PLCLCCU.FZACAM = 0d;
          PLCLCCU.FZARAT = APIBH.getARAT();
          IN92 = PLCLCCU.CCLCCUR();
          XXADED = PLCLCCU.FZACAM;
       } else {
          XXADED = XXCDED;
          PLCLCCU.FZARAT = APIBH.getARAT();
          PLCLCCU.FZRAFA = 0;
          PLCLCCU.FZCUAM = 0d;
          PLCLCCU.FZACAM = 0d;
          PLCLCCU.FZTEST = 0;
          PLCLCCU.FZDCAM = 0;
          PLCLCCU.FZLCDC = 0;
          PLCLCCU.FZTX15.clear();
          PLCLCCU.FZVERR = 0;
          PLCLCCU.FZMSGI.clear();
          PLCLCCU.FZMSGN = 0;
          PLCLCCU.FZMSGA.clear();
          IN92 = PLCLCCU.CCLCCUR();
       }                  
       //  Set params for CRTVACC (Entry of Deductible VAT)
       PXCRTVAC.PPCONO = CR040.getCONO();
       PXCRTVAC.PPDIVI.move(CR040.getDIVI());
       PXCRTVAC.PPEVEN.move("AP50");
       PXCRTVAC.PPACTY.move("291 ");
       if (!CR040.getSPYN().isBlank()) {
          PXCRTVAC.PPSUNO.move(CR040.getSPYN());
       } else {
          PXCRTVAC.PPSUNO.move(CR040.getSUNO());
       }
       PXCRTVAC.PPFDAT = CR040.getACDT();
       PXCRTVAC.PPCMTP = LDAZD.CMTP;
       PXCRTVAC.PPLANC.move(LDAZD.LANC);
       PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);
       PXCRTVAC.PPVTCD = CR040.getVTCD();
       PXCRTVAC.PPCFI1.moveLeft(XVCFI1);
       PXCRTVAC.CRTVACC();
       CR040.setACA()[0].move(PXCRTVAC.PPAIT1);
       CR040.setACA()[1].move(PXCRTVAC.PPAIT2);
       CR040.setACA()[2].move(PXCRTVAC.PPAIT3);
       CR040.setACA()[3].move(PXCRTVAC.PPAIT4);
       CR040.setACA()[4].move(PXCRTVAC.PPAIT5);
       CR040.setACA()[5].move(PXCRTVAC.PPAIT6);
       CR040.setACA()[6].move(PXCRTVAC.PPAIT7);
       if (CR040.getACA()[0].EQ("=       ")) {
          CR040.setACA()[0].move(savedAIT1_VAT1);
       }
       if (CR040.getACA()[1].EQ("=       ")) {
          CR040.setACA()[1].move(savedAIT2_VAT1);
       }
       if (CR040.getACA()[2].EQ("=       ")) {
          CR040.setACA()[2].move(savedAIT3_VAT1);
       }
       if (CR040.getACA()[3].EQ("=       ")) {
          CR040.setACA()[3].move(savedAIT4_VAT1);
       }
       if (CR040.getACA()[4].EQ("=       ")) {
          CR040.setACA()[4].move(savedAIT5_VAT1);
       }
       if (CR040.getACA()[5].EQ("=       ")) {
          CR040.setACA()[5].move(savedAIT6_VAT1);
       }
       if (CR040.getACA()[6].EQ("=       ")) {
          CR040.setACA()[6].move(savedAIT7_VAT1);
       }
       IN91 = true;
       CR040.SETLL("10", CR040.getKey("10", 15));
       while (CR040.READE("10", CR040.getKey("10", 15))) {
          if (CR040.getPINC() == 0 ||
              CR040.getPINC() == 4) {
             IN91 = false;
             break;
          }
       }
       if (IN91) {
          XXTRNO++;
          CR040.setTRNO(XXTRNO);
          CR040.setCUAM(XXCDED);
          CR040.setACAM(XXADED);
          CR040.setACTY().move("291 ");      
          CR040.setVTAM(0d);
          CR040.setACA4(CR040.getCUAM());
          CR040.setDBCR(' ');
          if (CRS750DS.getPBDCNY() == 1) {
             if (CR040.getCUAM() < (0d - EPS_2)) {
                CR040.setDBCR(CRS750DS.getPBDBNG());
             } else {
                CR040.setDBCR(CRS750DS.getPBDBPS());
             }
          }         
          CR040.setDEDA(APS36XDS.getEFDEDA());          
          ADAT04();
          CR040.setCVT1(0d);
          CR040.setCVT2(0d);
          CR040.setVTP1(VATPC.getVTD1());
          CR040.setVTP2(0d);
          CR040.setVTD1(VATPC.getVTD1());
          CR040.setEXN1(0);
          CR040.setEXI1().clear();
          CR040.setEXN2(0);
          CR040.setEXI2().clear();
          CR040.setEXN3(0);
          CR040.setEXI3().clear();
          CR040.setEXN4(0);
          CR040.setEXI4().clear();
          CR040.setEXN5(0);
          CR040.setEXI5().clear();                                  
          //   Update for GL Extra info 21 when VRNO exists                        
          if (!CR040.getVRNO().isBlank()) {                         
             insertExtraInfo(21, CR040.getVRNO());                        
          } else if (hasSupplierVRNO()) {                        
             insertExtraInfo(21, supVRNO);                          
          }                        
          CR040.WRITE("00");                         
          
          XTCUAM -= XXCDED;
          XTACAM -= XXADED;                        
          clearExtraInfo(21);                        
          CR040.setACTY().clear();                         
       } else { 
          if (CR040.CHAIN_LOCK("10", CR040.getKey("10"))) {
             CR040.setCUAM(CR040.getCUAM() + XXCDED);
             CR040.setACAM(CR040.getACAM() + XXADED);
             CR040.setDBCR(' ');
             if (CRS750DS.getPBDCNY() == 1) {
                if (CR040.getCUAM() < (0d - EPS_2)) {
                   CR040.setDBCR(CRS750DS.getPBDBNG());
                } else {
                   CR040.setDBCR(CRS750DS.getPBDBPS());
                }
             }
             if (equals(CR040.getACAM(), 2, 0d) ||
                 equals(CR040.getCUAM(), 2, 0d)) {
                // Delete record
                CR040.DELET("10");
             } else {            
                CR040.UPDAT("10");
                XTCUAM -= XXCDED;
                XTACAM -= XXADED;
             }
          }   
       }   
       
       // Create a deductible VAT cost record                          
       // Reverse amounts used for the VAT record
       XXCDED = -(XXCDED);
       XXADED = -(XXADED);
       //  Set params for CRTVACC (Entry of Deductible VAT)                        
       PXCRTVAC.PPCONO = CR040.getCONO();                        
       PXCRTVAC.PPDIVI.move(CR040.getDIVI());                          
       PXCRTVAC.PPEVEN.move("AP50");                          
       PXCRTVAC.PPACTY.move("293 ");                          
       if (!CR040.getSPYN().isBlank()) {                         
          PXCRTVAC.PPSUNO.move(CR040.getSPYN());                          
       } else {                          
          PXCRTVAC.PPSUNO.move(CR040.getSUNO());                          
       }                        
       PXCRTVAC.PPFDAT = CR040.getACDT();                           
       PXCRTVAC.PPCMTP = LDAZD.CMTP;                          
       PXCRTVAC.PPLANC.move(LDAZD.LANC);                         
       PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);                          
       PXCRTVAC.PPVTCD = CR040.getVTCD();                        
       PXCRTVAC.PPCFI1.moveLeft(XVCFI1);                         
       PXCRTVAC.CRTVACC();                        
       CR040.setACA()[0].move(PXCRTVAC.PPAIT1);                        
       CR040.setACA()[1].move(PXCRTVAC.PPAIT2);                        
       CR040.setACA()[2].move(PXCRTVAC.PPAIT3);                        
       CR040.setACA()[3].move(PXCRTVAC.PPAIT4);                        
       CR040.setACA()[4].move(PXCRTVAC.PPAIT5);                        
       CR040.setACA()[5].move(PXCRTVAC.PPAIT6);                        
       CR040.setACA()[6].move(PXCRTVAC.PPAIT7);                        
       if (CR040.getACA()[0].EQ("=       ")) {                         
          CR040.setACA()[0].move(savedAIT1_Cost);                         
       }                        
       if (CR040.getACA()[1].EQ("=       ")) {                         
          CR040.setACA()[1].move(savedAIT2_Cost);                         
       }                        
       if (CR040.getACA()[2].EQ("=       ")) {                         
          CR040.setACA()[2].move(savedAIT3_Cost);                         
       }                        
       if (CR040.getACA()[3].EQ("=       ")) {                         
          CR040.setACA()[3].move(savedAIT4_Cost);                         
       }                        
       if (CR040.getACA()[4].EQ("=       ")) {                         
          CR040.setACA()[4].move(savedAIT5_Cost);                         
       }                        
       if (CR040.getACA()[5].EQ("=       ")) {                         
          CR040.setACA()[5].move(savedAIT6_Cost);                         
       }                        
       if (CR040.getACA()[6].EQ("=       ")) {                         
          CR040.setACA()[6].move(savedAIT7_Cost);                         
       }                        
       IN91 = true;
       CR040.SETLL("10", CR040.getKey("10", 15));
       while (CR040.READE("10", CR040.getKey("10", 15))) {
          if (CR040.getPINC() == 0 ||
              CR040.getPINC() == 4) {
             IN91 = false;
             break;
          }
       }            
       if (IN91) {
          XXTRNO++;
          CR040.setTRNO(XXTRNO);
          CR040.setACTY().move("293 ");                          
          CR040.setCUAM(XXCDED);
          CR040.setACAM(XXADED);
          CR040.setVTAM(0d);                         
          CR040.setACA4(CR040.getCUAM());                        
          CR040.setDBCR(' ');                        
          if (CRS750DS.getPBDCNY() == 1) {                          
             if (CR040.getCUAM() < (0d - EPS_2)) {                        
                CR040.setDBCR(CRS750DS.getPBDBNG());                         
             } else {                          
                CR040.setDBCR(CRS750DS.getPBDBPS());                         
             }                        
          }                                 
          CR040.setDEDA(APS36XDS.getEFDEDA());          
          ADAT04();
          CR040.setCVT1(-(XXCDED));
          CR040.setCVT2(0d);                         
          CR040.setVTP1(VATPC.getVTD1());                        
          CR040.setVTP2(0d);
          CR040.setVTD1(VATPC.getVTD1());          
          CR040.setEXN1(0);                          
          CR040.setEXI1().clear();                         
          CR040.setEXN2(0);                          
          CR040.setEXI2().clear();                         
          CR040.setEXN3(0);                          
          CR040.setEXI3().clear();                         
          CR040.setEXN4(0);                          
          CR040.setEXI4().clear();                         
          CR040.setEXN5(0);                          
          CR040.setEXI5().clear();                                  
          //   Update for GL Extra info 21 when VRNO exists                        
          if (!CR040.getVRNO().isBlank()) {                         
             insertExtraInfo(21, CR040.getVRNO());                        
          } else if (hasSupplierVRNO()) {                        
             insertExtraInfo(21, supVRNO);                          
          }                        
          CR040.WRITE("00");                         
          
          XTCUAM -= XXCDED;
          XTACAM -= XXADED;                        
          clearExtraInfo(21);                        
          CR040.setACTY().clear();                         
       } else { 
          if (CR040.CHAIN_LOCK("10", CR040.getKey("10"))) {            
             CR040.setCUAM(CR040.getCUAM() + XXCDED);
             CR040.setACAM(CR040.getACAM() + XXADED);
             CR040.setCVT1(CR040.getCVT1() - XXCDED);
             CR040.setDBCR(' ');
             if (CRS750DS.getPBDCNY() == 1) {
                if (CR040.getCUAM() < (0d - EPS_2)) {
                   CR040.setDBCR(CRS750DS.getPBDBNG());
                } else {
                   CR040.setDBCR(CRS750DS.getPBDBPS());
                }
             }
             if (equals(CR040.getACAM(), 2, 0d) ||
                 equals(CR040.getCUAM(), 2, 0d)) {
                // Delete record
                CR040.DELET("10");
             } else {            
                CR040.UPDAT("10");
                XTCUAM -= XXCDED;
                XTACAM -= XXADED;
             }
          }   
       }   
    }                        
    
   /**
   *    createDeductibleVAT_2 - Create a second deductible VAT record in FCR040
   */
   public void createDeductibleVAT_2() {                        
      XXCDED = -(XDVTMX);
      //    Convert - Currency --> Local currency
      if (APIBH.getCUCD().NE(LDAZD.LOCD) &&
          APIBH.getDIVI().EQ(LDAZD.DIVI) ||
          APIBH.getCUCD().NE(MNDIV.getLOCD()) &&
          APIBH.getDIVI().NE(LDAZD.DIVI)) {
         PLCLCCU.FZCUAM = XXCDED;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZARAT = APIBH.getARAT();
         IN92 = PLCLCCU.CCLCCUR();
         XXADED = PLCLCCU.FZACAM;
      } else {
         XXADED = XXCDED;
         PLCLCCU.FZARAT = APIBH.getARAT();
         PLCLCCU.FZRAFA = 0;
         PLCLCCU.FZCUAM = 0d;
         PLCLCCU.FZACAM = 0d;
         PLCLCCU.FZTEST = 0;
         PLCLCCU.FZDCAM = 0;
         PLCLCCU.FZLCDC = 0;
         PLCLCCU.FZTX15.clear();
         PLCLCCU.FZVERR = 0;
         PLCLCCU.FZMSGI.clear();
         PLCLCCU.FZMSGN = 0;
         PLCLCCU.FZMSGA.clear();
         IN92 = PLCLCCU.CCLCCUR();
      }       
      //  Set params for CRTVACC (Entry of Deductible VAT)                        
      PXCRTVAC.PPCONO = CR040.getCONO();                        
      PXCRTVAC.PPDIVI.move(CR040.getDIVI());                          
      PXCRTVAC.PPEVEN.move("AP50");                          
      PXCRTVAC.PPACTY.move("292 ");                          
      if (!CR040.getSPYN().isBlank()) {                         
         PXCRTVAC.PPSUNO.move(CR040.getSPYN());                          
      } else {                          
         PXCRTVAC.PPSUNO.move(CR040.getSUNO());                          
      }                           
      PXCRTVAC.PPFDAT = CR040.getACDT();                        
      PXCRTVAC.PPCMTP = LDAZD.CMTP;                          
      PXCRTVAC.PPLANC.move(LDAZD.LANC);                         
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);                          
      PXCRTVAC.PPVTCD = CR040.getVTCD();                        
      PXCRTVAC.PPCFI1.moveLeft(XVCFI1);                         
      PXCRTVAC.CRTVACC();                           
      CR040.setACA()[0].move(PXCRTVAC.PPAIT1);                        
      CR040.setACA()[1].move(PXCRTVAC.PPAIT2);                        
      CR040.setACA()[2].move(PXCRTVAC.PPAIT3);                           
      CR040.setACA()[3].move(PXCRTVAC.PPAIT4);                           
      CR040.setACA()[4].move(PXCRTVAC.PPAIT5);                           
      CR040.setACA()[5].move(PXCRTVAC.PPAIT6);                           
      CR040.setACA()[6].move(PXCRTVAC.PPAIT7);                        
      if (CR040.getACA()[0].EQ("=       ")) {                         
         CR040.setACA()[0].move(savedAIT1_VAT2);                         
      }                        
      if (CR040.getACA()[1].EQ("=       ")) {                         
         CR040.setACA()[1].move(savedAIT2_VAT2);                         
      }                        
      if (CR040.getACA()[2].EQ("=       ")) {                         
         CR040.setACA()[2].move(savedAIT3_VAT2);                         
      }                        
      if (CR040.getACA()[3].EQ("=       ")) {                         
         CR040.setACA()[3].move(savedAIT4_VAT2);                         
      }                        
      if (CR040.getACA()[4].EQ("=       ")) {                         
         CR040.setACA()[4].move(savedAIT5_VAT2);                         
      }                        
      if (CR040.getACA()[5].EQ("=       ")) {                         
         CR040.setACA()[5].move(savedAIT6_VAT2);                         
      }                        
      if (CR040.getACA()[6].EQ("=       ")) {                         
         CR040.setACA()[6].move(savedAIT7_VAT2);                         
      }                        
      IN91 = true;
      CR040.SETLL("10", CR040.getKey("10", 15));
      while (CR040.READE("10", CR040.getKey("10", 15))) {
         if (CR040.getPINC() == 0 ||
             CR040.getPINC() == 4) {
            IN91 = false;
            break;
         }
      }
      if (IN91) {
         XXTRNO++;
         CR040.setTRNO(XXTRNO);
         CR040.setCUAM(XXCDED);
         CR040.setACAM(XXADED);
         CR040.setACTY().move("292 ");                                      
         CR040.setVTAM(0d);                         
         CR040.setACA4(CR040.getCUAM());                        
         CR040.setDBCR(' ');                        
         if (CRS750DS.getPBDCNY() == 1) {                          
            if (CR040.getCUAM() < (0d - EPS_2)) {                        
               CR040.setDBCR(CRS750DS.getPBDBNG());                         
            } else {                          
               CR040.setDBCR(CRS750DS.getPBDBPS());                         
            }                        
         }                                 
         CR040.setDEDA(APS36XDS.getEFDEDA());          
         ADAT04();
         CR040.setCVT1(0d);                         
         CR040.setCVT2(0d);                         
         CR040.setVTP1(0d);                         
         CR040.setVTP2(VATPC.getVTD1());
         CR040.setVTD1(VATPC.getVTD1());           
         CR040.setEXN1(0);                          
         CR040.setEXI1().clear();                         
         CR040.setEXN2(0);                          
         CR040.setEXI2().clear();                         
         CR040.setEXN3(0);                          
         CR040.setEXI3().clear();                         
         CR040.setEXN4(0);                          
         CR040.setEXI4().clear();                         
         CR040.setEXN5(0);                          
         CR040.setEXI5().clear();                                 
         //   Update for GL Extra info 21 when VRNO exists                        
         if (!CR040.getVRNO().isBlank()) {                         
            insertExtraInfo(21, CR040.getVRNO());                           
         } else if (hasSupplierVRNO()) {                        
            insertExtraInfo(21, supVRNO);                          
         }                        
         CR040.WRITE("00");                         
         
         XTCUAM -= XXCDED;
         XTACAM -= XXADED;                        
         clearExtraInfo(21);                        
         CR040.setACTY().clear();                         
      } else { 
         if (CR040.CHAIN_LOCK("10", CR040.getKey("10"))) {
            CR040.setCUAM(CR040.getCUAM() + XXCDED);
            CR040.setACAM(CR040.getACAM() + XXADED);
            CR040.setDBCR(' ');
            if (CRS750DS.getPBDCNY() == 1) {
               if (CR040.getCUAM() < (0d - EPS_2)) {
                  CR040.setDBCR(CRS750DS.getPBDBNG());
               } else {
                  CR040.setDBCR(CRS750DS.getPBDBPS());
               }
            }
            if (equals(CR040.getACAM(), 2, 0d) ||
                equals(CR040.getCUAM(), 2, 0d)) {
               // Delete record
               CR040.DELET("10");
            } else {            
               CR040.UPDAT("10");
               XTCUAM -= XXCDED;
               XTACAM -= XXADED;
            }
         }   
      }   
       
      // Create a second deductible VAT cost record                         
      // Reverse amounts used for the VAT record
      XXCDED = -(XXCDED);
      XXADED = -(XXADED);
      //  Set params for CRTVACC (Entry of Deductible VAT)                        
      PXCRTVAC.PPCONO = CR040.getCONO();                        
      PXCRTVAC.PPDIVI.move(CR040.getDIVI());                          
      PXCRTVAC.PPEVEN.move("AP50");                          
      PXCRTVAC.PPACTY.move("294 ");                          
      if (!CR040.getSPYN().isBlank()) {                         
         PXCRTVAC.PPSUNO.move(CR040.getSPYN());                          
      } else {                          
         PXCRTVAC.PPSUNO.move(CR040.getSUNO());                          
      }                        
      PXCRTVAC.PPFDAT = CR040.getACDT();                        
      PXCRTVAC.PPCMTP = LDAZD.CMTP;                          
      PXCRTVAC.PPLANC.move(LDAZD.LANC);                         
      PXCRTVAC.PPDCFM.moveRight(LDAZD.DCFM);                          
      PXCRTVAC.PPVTCD = CR040.getVTCD();                        
      PXCRTVAC.PPCFI1.moveLeft(XVCFI1);                         
      PXCRTVAC.CRTVACC();                        
      CR040.setACA()[0].move(PXCRTVAC.PPAIT1);                        
      CR040.setACA()[1].move(PXCRTVAC.PPAIT2);                        
      CR040.setACA()[2].move(PXCRTVAC.PPAIT3);                           
      CR040.setACA()[3].move(PXCRTVAC.PPAIT4);                           
      CR040.setACA()[4].move(PXCRTVAC.PPAIT5);                        
      CR040.setACA()[5].move(PXCRTVAC.PPAIT6);                           
      CR040.setACA()[6].move(PXCRTVAC.PPAIT7);                           
      if (CR040.getACA()[0].EQ("=       ")) {                         
         CR040.setACA()[0].move(savedAIT1_Cost);                         
      }                        
      if (CR040.getACA()[1].EQ("=       ")) {                         
         CR040.setACA()[1].move(savedAIT2_Cost);                         
      }                                 
      if (CR040.getACA()[2].EQ("=       ")) {                         
         CR040.setACA()[2].move(savedAIT3_Cost);                         
      }                        
      if (CR040.getACA()[3].EQ("=       ")) {                         
         CR040.setACA()[3].move(savedAIT4_Cost);                         
      }                        
      if (CR040.getACA()[4].EQ("=       ")) {                         
         CR040.setACA()[4].move(savedAIT5_Cost);                         
      }                        
      if (CR040.getACA()[5].EQ("=       ")) {                         
         CR040.setACA()[5].move(savedAIT6_Cost);                         
      }                        
      if (CR040.getACA()[6].EQ("=       ")) {                         
         CR040.setACA()[6].move(savedAIT7_Cost);                         
      }                        
      IN91 = true;
      CR040.SETLL("10", CR040.getKey("10", 15));
      while (CR040.READE("10", CR040.getKey("10", 15))) {
         if (CR040.getPINC() == 0 ||
             CR040.getPINC() == 4) {
            IN91 = false;
            break;
         }
      }            
      if (IN91) {
         XXTRNO++;
         CR040.setTRNO(XXTRNO);
         CR040.setACTY().move("294 ");                          
         CR040.setCUAM(XXCDED);
         CR040.setACAM(XXADED);
         CR040.setVTAM(0d);                         
         CR040.setACA4(CR040.getCUAM());                        
         CR040.setDBCR(' ');                        
         if (CRS750DS.getPBDCNY() == 1) {                          
            if (CR040.getCUAM() < (0d - EPS_2)) {                        
               CR040.setDBCR(CRS750DS.getPBDBNG());                         
            } else {                          
               CR040.setDBCR(CRS750DS.getPBDBPS());                         
            }                        
         }                              
         CR040.setDEDA(APS36XDS.getEFDEDA());          
         ADAT04();
         CR040.setCVT1(0d);                         
         CR040.setCVT2(-(XXCDED));
         CR040.setVTP1(0d);                         
         CR040.setVTP2(VATPC.getVTD1());
         CR040.setVTD1(VATPC.getVTD1());         
         CR040.setEXN1(0);                          
         CR040.setEXI1().clear();                         
         CR040.setEXN2(0);                          
         CR040.setEXI2().clear();                         
         CR040.setEXN3(0);                          
         CR040.setEXI3().clear();                         
         CR040.setEXN4(0);                          
         CR040.setEXI4().clear();                         
         CR040.setEXN5(0);                          
         CR040.setEXI5().clear();                                 
         //   Update for GL Extra info 21 when VRNO exists                        
         if (!CR040.getVRNO().isBlank()) {                         
            insertExtraInfo(21, CR040.getVRNO());                        
         } else if (hasSupplierVRNO()) {                        
            insertExtraInfo(21, supVRNO);                          
         }                        
         CR040.WRITE("00");                         
         
         XTCUAM -= XXCDED;
         XTACAM -= XXADED;                        
         clearExtraInfo(21);                        
         CR040.setACTY().clear();                         
      } else { 
         if (CR040.CHAIN_LOCK("10", CR040.getKey("10"))) {
            CR040.setCUAM(CR040.getCUAM() + XXCDED);
            CR040.setACAM(CR040.getACAM() + XXADED);
            CR040.setCVT2(CR040.getCVT2() - XXCDED);
            CR040.setDBCR(' ');
            if (CRS750DS.getPBDCNY() == 1) {
               if (CR040.getCUAM() < (0d - EPS_2)) {
                  CR040.setDBCR(CRS750DS.getPBDBNG());
               } else {
                  CR040.setDBCR(CRS750DS.getPBDBPS());
               }
            }
            if (equals(CR040.getACAM(), 2, 0d) ||
                equals(CR040.getCUAM(), 2, 0d)) {
               // Delete record
               CR040.DELET("10");
            } else {            
               CR040.UPDAT("10");
               XTCUAM -= XXCDED;
               XTACAM -= XXADED;
            }
         }   
      }   
   }

   /**
   * End of program
   */
   public void SETLR() {
      // Delete override - FAPIBH
      session.DLTOVR("FAPIBH");
      // Delete records in CJBCMD
      JBCMD.setBJNO().moveLeftPad(saveBJNO);
      JBCMD.DELET("00", JBCMD.getKey("00", 1));
      // End of program - clear fields in the pool
      INLR = true;
      super.SETLR(INLR);
   }

   /**
   * Init subroutine
   */
   public void INIT() {
      saveBJNO.moveLeftPad(PXBJNO);
      // Override database file - FAPIBH
      JBCMD.setBJNO().moveLeftPad(PXBJNO);
      JBCMD.setBJLI().moveLeftPad("01");
      if (!JBCMD.CHAIN("00", JBCMD.getKey("00"))) {
			CRCommon.generateDumpInfo("No records in CJBCMD with jobnumber :" + PXBJNO + " and linenumber : 01");
			SETLR();
			return;
      }
      session.OVRDBF(JBCMD.getFILE().toStringRTrim(), true);
		KQLOG.D("QCMD: " + JBCMD.getQCMD().toStringRTrim());
	   if (apCall("QCMDEXC", JBCMD.getQCMD())) {
			CRCommon.generateDumpInfo("Program terminated, see stacktrace");
			SETLR();
			return;
		}
      //Get select data
      APS455DS.clear();
      JBCMD.setBJLI().move("99");
      if (JBCMD.CHAIN("00", JBCMD.getKey("00"))) {
         APS455DS.set().moveLeft(JBCMD.getQCMD());
      }
      // Invoice batch operation
      IBOP = APS455DS.getZWIBOP();
      // Check if called manually from APS455
      manualCall = toBoolean(APS455DS.getZWMANU());
      // Check if we are running in a Russian market 
      market_RU = checkMarket("CRRU01");
   }

   /**
   * No valid operation was received.
   * A dumplog is generated and SETLR() is called.
   */
   public void operationError() {
      CRCommon.generateDumpInfo("No valid operation was received in CJBCMD with jobnumber :" + PXBJNO);
      SETLR();
   }

   // Movex MDB definitions
   public mvx.db.dta.CJBCMD JBCMD;
   public mvx.db.dta.FAPIBH APIBH;
   public mvx.db.dta.FAPIBL APIBL;
   public mvx.db.dta.CMNDIV MNDIV;
   public mvx.db.dta.CMNCMP MNCMP;
   public mvx.db.dta.CSYPAR SYPAR;
   public mvx.db.dta.CSYTAB SYTAB;
   public mvx.db.dta.CIDMAS IDMAS;
   public mvx.db.dta.CIDVEN IDVEN;
   public mvx.db.dta.CSUDIV SUDIV;
   public mvx.db.dta.FCR040 CR040;
   public mvx.db.dta.MPFAMF PFAMF;
   public mvx.db.dta.MPLINE PLINE; 	
   public mvx.db.dta.MPCELE PCELE;
   public mvx.db.dta.MPSUPR PSUPR;
   public mvx.db.dta.FPCASH PCASH;
   public mvx.db.dta.CIDADR IDADR;
   public mvx.db.dta.FGINHE GINHE;
   public mvx.db.dta.FGINHC GINHC;
   public mvx.db.dta.FGINLI GINLI;
   public mvx.db.dta.FGINLC GINLC;
   public mvx.db.dta.FGINDH GINDH;
   public mvx.db.dta.FGINPA GINPA;
   public mvx.db.dta.FCHACC CHACC;
   public mvx.db.dta.MPAGRS PAGRS;
   public mvx.db.dta.FPLEDG PLEDG;
   public mvx.db.dta.FPLEDX PLEDX;
   public mvx.db.dta.FGLEDG GLEDG;
   public mvx.db.dta.FPPPAY PPPAY;
   public mvx.db.dta.CVATPC VATPC;
   public mvx.db.dta.MPSUPS PSUPS;
   // Movex MDB definitions end

   public void initMDB() {
      JBCMD = (mvx.db.dta.CJBCMD)getMDB("CJBCMD", JBCMD);
      APIBH = (mvx.db.dta.FAPIBH)getMDB("FAPIBH", APIBH);
      APIBL = (mvx.db.dta.FAPIBL)getMDB("FAPIBL", APIBL);
      MNDIV = (mvx.db.dta.CMNDIV)getMDB("CMNDIV", MNDIV);
      MNCMP = (mvx.db.dta.CMNCMP)getMDB("CMNCMP", MNCMP);
      SYPAR = (mvx.db.dta.CSYPAR)getMDB("CSYPAR", SYPAR);
      SYTAB = (mvx.db.dta.CSYTAB)getMDB("CSYTAB", SYTAB);
      IDMAS = (mvx.db.dta.CIDMAS)getMDB("CIDMAS", IDMAS);
      IDVEN = (mvx.db.dta.CIDVEN)getMDB("CIDVEN", IDVEN);
      SUDIV = (mvx.db.dta.CSUDIV)getMDB("CSUDIV", SUDIV);
      CR040 = (mvx.db.dta.FCR040)getMDB("FCR040", CR040);
      PFAMF = (mvx.db.dta.MPFAMF)getMDB("MPFAMF", PFAMF);
      PLINE = (mvx.db.dta.MPLINE)getMDB("MPLINE", PLINE);
      PCELE = (mvx.db.dta.MPCELE)getMDB("MPCELE", PCELE);
      PSUPR = (mvx.db.dta.MPSUPR)getMDB("MPSUPR", PSUPR);
      PCASH = (mvx.db.dta.FPCASH)getMDB("FPCASH", PCASH);
      IDADR = (mvx.db.dta.CIDADR)getMDB("CIDADR", IDADR);
      GINHE = (mvx.db.dta.FGINHE)getMDB("FGINHE", GINHE);
      GINHC = (mvx.db.dta.FGINHC)getMDB("FGINHC", GINHC);
      GINLI = (mvx.db.dta.FGINLI)getMDB("FGINLI", GINLI);
      GINLC = (mvx.db.dta.FGINLC)getMDB("FGINLC", GINLC);
      GINDH = (mvx.db.dta.FGINDH)getMDB("FGINDH", GINDH);
      GINPA = (mvx.db.dta.FGINPA)getMDB("FGINPA", GINPA);
      CHACC = (mvx.db.dta.FCHACC)getMDB("FCHACC", CHACC);
      PAGRS = (mvx.db.dta.MPAGRS)getMDB("MPAGRS", PAGRS);
      PLEDG = (mvx.db.dta.FPLEDG)getMDB("FPLEDG", PLEDG); 	
      PLEDX = (mvx.db.dta.FPLEDX)getMDB("FPLEDX", PLEDX); 	
      GLEDG = (mvx.db.dta.FGLEDG)getMDB("FGLEDG", GLEDG);
      PPPAY = (mvx.db.dta.FPPPAY)getMDB("FPPPAY", PPPAY);
      VATPC = (mvx.db.dta.CVATPC)getMDB("CVATPC", VATPC);
      PSUPS = (mvx.db.dta.MPSUPS)getMDB("MPSUPS", PSUPS);
   }

   public cPXAPS450FncINdelete pAPS450Fnc_delete = null;
   public cPXAPS450FncOPlockForUpdAPL pAPS450Fnc_lockForUpdAPL = null;
   public cPXAPS450FncOPlockForPrint pAPS450Fnc_lockForPrint = null;
   public cPXAPS450FncOPunlock pAPS450Fnc_unlock = null;
   public cPXAPS450FncOPvalidateInv pAPS450Fnc_validateInv = null;

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncINdelete get_pAPS450Fnc_delete() {
      if (pAPS450Fnc_delete == null) {
         cPXAPS450FncINdelete newPlist = new cPXAPS450FncINdelete();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_delete.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_delete;
      }
   }

   /**
   * Calling APS450Fnc with pAPS450Fnc_delete as a transaction.
   */
   @Transaction(name=cPXAPS450FncINdelete.LOGICAL_NAME)
   public void transaction_APS450FncINdelete() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_delete);
      LDAZZ.FPNM.move(this.DSPGM);
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPlockForPrint get_pAPS450Fnc_lockForPrint() {
      if (pAPS450Fnc_lockForPrint == null) {
         cPXAPS450FncOPlockForPrint newPlist = new cPXAPS450FncOPlockForPrint();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_lockForPrint.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_lockForPrint;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPlockForUpdAPL get_pAPS450Fnc_lockForUpdAPL() {
      if (pAPS450Fnc_lockForUpdAPL == null) {
         cPXAPS450FncOPlockForUpdAPL newPlist = new cPXAPS450FncOPlockForUpdAPL();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_lockForUpdAPL.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_lockForUpdAPL;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPunlock get_pAPS450Fnc_unlock() {
      if (pAPS450Fnc_unlock == null) {
         cPXAPS450FncOPunlock newPlist = new cPXAPS450FncOPunlock();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_unlock.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_unlock;
      }
   }

   /**
   * Instantiates and sets formatting for the plist if not already instantiated.
   * @return
   *    A reference to the plist.
   */
   public cPXAPS450FncOPvalidateInv get_pAPS450Fnc_validateInv() {
      if (pAPS450Fnc_validateInv == null) {
         cPXAPS450FncOPvalidateInv newPlist = new cPXAPS450FncOPvalidateInv();
         newPlist.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return newPlist;
      } else {
         pAPS450Fnc_validateInv.setFormatting(LDAZD.DTFM, LDAZD.DSEP, LDAZD.DCFM);
         return pAPS450Fnc_validateInv;
      }
   }

   /**
   * Calling APS450Fnc with pAPS450Fnc_validateInv as a transaction.
   */
   @Transaction(name=cPXAPS450FncOPvalidateInv.LOGICAL_NAME)
   public void transaction_APS450FncOPvalidateInv() {
      XFPGNM.move(LDAZZ.FPNM);
      LDAZZ.FPNM.move(this.DSPGM);
      apCall("APS450Fnc", pAPS450Fnc_validateInv);
      LDAZZ.FPNM.move(this.DSPGM);
   }

   public void unpackEntryParams(Object o) {//extract entry param
      MvxRecord mr = (MvxRecord)o;
      mr.reset();
      mr.getString(PXBJNO);
   }

   public void returnEntryParams(Object o) {//return entry param
      MvxRecord mr = (MvxRecord)o;
      mr.reset();
      mr.set(PXBJNO);
   }

   public void PLCHKIFSyncTo() {
      PLCHKIF.FTCSCD.move(PLCRTVT.FTCSCD);
      PLCHKIF.FTECAR.move(PLCRTVT.FTECAR);
      PLCHKIF.FTTASK = PLCRTVT.FTTASK;
      PLCHKIF.FTDIVI.move(PLCRTVT.FTDIVI);
      PLCHKIF.FTVTCD = PLCRTVT.FTVTCD;
      PLCHKIF.FTSUNO.move(PLCRTVT.FTSUNO);
      PLCHKIF.FTCONO = PLCRTVT.FTCONO;
      PLCHKIF.FTCUNO.move(PLCRTVT.FTCUNO);
   }

   public void PLCHKIFSyncFrom() {
      PLCRTVT.FTCSCD.move(PLCHKIF.FTCSCD);
      PLCRTVT.FTECAR.move(PLCHKIF.FTECAR);
      PLCRTVT.FTTASK = PLCHKIF.FTTASK;
      PLCRTVT.FTDIVI.move(PLCHKIF.FTDIVI);
      PLCRTVT.FTVTCD = PLCHKIF.FTVTCD;
      PLCRTVT.FTSUNO.move(PLCHKIF.FTSUNO);
      PLCRTVT.FTCONO = PLCHKIF.FTCONO;
      PLCRTVT.FTCUNO.move(PLCHKIF.FTCUNO);
   }

   public MvxStruct rXVCFI1 = new MvxStruct(8);
   public MvxString XVCFI1 = rXVCFI1.newString(0, 8);
   public MvxString XVBSCD = rXVCFI1.newInt(0, 3);
   public MvxString XVFTCO = rXVCFI1.newInt(3, 3);
   public MvxString XVECAR = rXVCFI1.newInt(6, 2);

   public MvxStruct rGLS040_BJNO = new MvxStruct(18);
   public MvxString GLS040_BJNO = rGLS040_BJNO.newString(0, 18);
   public MvxString GLS040_JBNO = rGLS040_BJNO.newString(0, 6);
   public MvxString GLS040_JBDT = rGLS040_BJNO.newString(6, 6);
   public MvxString GLS040_JBTM = rGLS040_BJNO.newString(12, 6);

   public MvxStruct rXXVTXT = new MvxStruct(40);
   public MvxString XXVTXT = rXXVTXT.newString(0, 40);
   public MvxString XXSUNO = rXXVTXT.newString(0, 10);
   public MvxString XXSINO = rXXVTXT.newString(11, 14);
   public MvxString XXINYR = rXXVTXT.newInt(25, 4);
   public MvxString XXALSU = rXXVTXT.newString(30, 10);

   public MvxStruct rDSINS0 = new MvxStruct(5);
   public MvxString DSINS0 = rDSINS0.newString(0, 5);
   public MvxString DSINS1 = rDSINS0.newChar(0);
   public MvxString DSINS2 = rDSINS0.newChar(1);
   public MvxString DSINS3 = rDSINS0.newChar(2);
   public MvxString DSINS4 = rDSINS0.newChar(3);
   public MvxString DSINS5 = rDSINS0.newChar(4);

   public MvxStruct rFESTKY = new MvxStruct(10);
   public MvxString FESTKY = rFESTKY.newString(0, 10);
   public MvxString FEFEID = rFESTKY.newString(0, 4);
   public MvxString FEFNCN = rFESTKY.newInt(4, 3);
   
   public MvxStruct rXXInvInfo = new MvxStruct(45); 	
   public MvxString XXInvInfo = rXXInvInfo.newString(0, 45); 	
   public MvxString XXInvoice = rXXInvInfo.newString(0, 24); 	
   public MvxString XXInvYear = rXXInvInfo.newInt(25, 4); 	

   //*STRUCDEF rKSTR{ 	
   public MvxStruct rKSTR = new MvxStruct(480); 	
   public MvxString KSTR = rKSTR.newString(0, 480); 	
   public MvxString KSTR01 = rKSTR.newString(0, 30); 	
   public MvxString KSTR02 = rKSTR.newString(30, 30); 	
   public MvxString KSTR03 = rKSTR.newString(60, 30); 	
   public MvxString KSTR04 = rKSTR.newString(90, 30); 	
   public MvxString KSTR05 = rKSTR.newString(120, 30); 	
   public MvxString KSTR06 = rKSTR.newString(150, 30); 	
   public MvxString KSTR07 = rKSTR.newString(180, 30); 	
   public MvxString KSTR08 = rKSTR.newString(210, 30); 	
   public MvxString KSTR09 = rKSTR.newString(240, 30); 	
   public MvxString KSTR10 = rKSTR.newString(270, 30); 	
   public MvxString KSTR11 = rKSTR.newString(300, 30); 	
   public MvxString KSTR12 = rKSTR.newString(330, 30); 	
   public MvxString KSTR13 = rKSTR.newString(360, 30); 	
   public MvxString KSTR14 = rKSTR.newString(390, 30); 	
   public MvxString KSTR15 = rKSTR.newString(420, 30); 	
   public MvxString KSTR16 = rKSTR.newString(450, 30); 	

   public void rXXRE20SyncTo() {
      E1SUNO.move(APIBH.getSUNO());
      E1PUNO.move(APIBH.getPUNO());
   }

   public void rXXRE20SyncFrom() {
      APIBH.setSUNO().move(E1SUNO);
      APIBH.setPUNO().move(E1PUNO);
   }

   public MvxStruct rXXRE20 = new MvxStruct(45); 	
   public MvxString XXRE20 = rXXRE20.newString(0, 45); 	
   public MvxString E1SUNO = rXXRE20.newString(0, 10);
   public MvxString E1PUNO = rXXRE20.newString(10, cRefPUNO.length());

   //*STRUCDEF rRATE991{
   public MvxStruct rRATE991 = new MvxStruct(45); 	
   public MvxString RATE991 = rRATE991.newString(0, 45); 	
   public MvxString ALPHA12 = rRATE991.newString(0, 12);
   
   public MvxRecord rMNGI03 = new MvxRecord();

   public void rMNGI03preCall() {// insert param into record for call
      rMNGI03.reset();
      rMNGI03.set(APIDS);
      rMNGI03.set(APMNGI03DS.getAPMNGI03DS());
      rMNGI03.set(APS36XDS.getAPS36XDS());
   }

   public void rMNGI03postCall() {// extract param from record after call
      rMNGI03.reset();
      rMNGI03.getString(APIDS);
      rMNGI03.getString(X5DS);
      rMNGI03.unGet();
      rMNGI03.getString(APMNGI03DS.setAPMNGI03DS());
      rMNGI03.getString(XFDS);
      rMNGI03.unGet();
      rMNGI03.getString(APS36XDS.setAPS36XDS());
   }

   public MvxRecord rMNGI01 = new MvxRecord();

   public void rMNGI01preCall() {// insert param into record for call
      rMNGI01.reset();
      rMNGI01.set(APIDS);
      rMNGI01.set(APMNGI01DS.getAPMNGI01DS());
      rMNGI01.set(APS36XDS.getAPS36XDS());
   }

   public void rMNGI01postCall() {// extract param from record after call
      rMNGI01.reset();
      rMNGI01.getString(APIDS);
      rMNGI01.getString(X3DS);
      rMNGI01.unGet();
      rMNGI01.getString(APMNGI01DS.setAPMNGI01DS());
      rMNGI01.getString(XFDS);
      rMNGI01.unGet();
      rMNGI01.getString(APS36XDS.setAPS36XDS());
   }

   public MvxRecord rMNGI02 = new MvxRecord();

   public void rMNGI02preCall() {// insert param into record for call
      rMNGI02.reset();
      rMNGI02.set(APIDS);
      rMNGI02.set(APMNGI02DS.getAPMNGI02DS());
      rMNGI02.set(APS36XDS.getAPS36XDS());
   }

   public void rMNGI02postCall() {// extract param from record after call

      rMNGI02.reset();
      rMNGI02.getString(APIDS);
      rMNGI02.getString(X4DS);
      rMNGI02.unGet();
      rMNGI02.getString(APMNGI02DS.setAPMNGI02DS());
      rMNGI02.getString(XFDS);
      rMNGI02.unGet();
      rMNGI02.getString(APS36XDS.setAPS36XDS());
   }

   public MvxRecord rMNGI04 = new MvxRecord();

   public void rMNGI04preCall() {// insert param into record for call
      rMNGI04.reset();
      rMNGI04.set(APIDS);
      rMNGI04.set(APS36XDS.getAPS36XDS());
      rMNGI04.set(APMNGI04DS.getAPMNGI04DS());
   }

   public void rMNGI04postCall() {// extract param from record after call

      rMNGI04.reset();
      rMNGI04.getString(APIDS);
      rMNGI04.getString(XFDS);
      rMNGI04.unGet();
      rMNGI04.getString(APS36XDS.setAPS36XDS());
      rMNGI04.getString(X8DS);
      rMNGI04.unGet();
      rMNGI04.getString(APMNGI04DS.setAPMNGI04DS());
   }

   //*PARAM rPL428{
   public MvxRecord rPL428 = new MvxRecord();// len = 266

   public void rPL428preCall() {// insert param into record for call
      rPL428.reset();
      rPL428.set(CRS428DS.getCRS428DS());
      rPL428.set(CRS428PDS.getCRS428PDS());
   }

   public void rPL428postCall() {// extract param from record after call
      rPL428.reset();
      rPL428.getString(CRS428DS.setCRS428DS());
      rPL428.getString(CRS428PDS.setCRS428PDS());
   }

   public MvxRecord pCCHGOVR = new MvxRecord();

   public MvxRecord pGLS040CL = new MvxRecord();

   public void pGLS040CLpreCall() {// insert param into record for call
      pGLS040CL.reset();
      pGLS040CL.set(JBCMD.getBJNO());
   }

   public void pGLS040CLpostCall() {// extract param from record after call
      pGLS040CL.reset();
      pGLS040CL.getString(JBCMD.setBJNO());
   }
      
   //*PARAM rMNG04X{
   public MvxRecord rMNG04X = new MvxRecord();

   public void rMNG04XpreCall() {
      rMNG04X.reset();
      rMNG04X.set(GLMNG04XDS.getGLMNG04XDS());
   }

   public void rMNG04XpostCall() {
      rMNG04X.reset();
      rMNG04X.getString(GLMNG04XDS.setGLMNG04XDS());
   }
   public sGLMNG04XDS GLMNG04XDS = new sGLMNG04XDS(this);

   public MvxRecord pMNS213 = new MvxRecord(); 	

   public sMNS213DS MNS213DS = new sMNS213DS(this); 	

   public MvxStruct rAPIDS = new MvxStruct(413);
   public MvxString APIDS = rAPIDS.newString(0, 413);
   public MvxString PXENV = rAPIDS.newChar(0);
   public MvxString PXOPC = rAPIDS.newString(1, 10);
   public MvxString PXIN60 = rAPIDS.newChar(11);
   public MvxString PXMSID = rAPIDS.newString(12, 7);
   public MvxString PXMSGD = rAPIDS.newString(19, 256);
   public MvxString PXMSG = rAPIDS.newString(275, 128);
   public MvxString PXCHID = rAPIDS.newString(403, 10);

   public cPLCHKIF PLCHKIF = new cPLCHKIF(this);
   public cPLCLCCU PLCLCCU = new cPLCLCCU(this);
   public cPXCRTVAC PXCRTVAC = new cPXCRTVAC(this);
   public cPLCRTVT PLCRTVT = new cPLCRTVT(this);
   public cPLCHKVO PLCHKVO = new cPLCHKVO(this);
   public cPXCHKTAB PXCHKTAB = new cPXCHKTAB(this);
   public cPLRTVFNC PLRTVFNC = new cPLRTVFNC(this);
   public cPXMNS210 PXMNS210 = new cPXMNS210(this);
   public cPXCHKOBJ PXCHKOBJ = new cPXCHKOBJ(this);

   public sAPS455DS APS455DS = new sAPS455DS(this);
   public sCRS750DS CRS750DS = new sCRS750DS(this);
   public sAPMNGI01DS APMNGI01DS = new sAPMNGI01DS(this);
   public sAPMNGI02DS APMNGI02DS = new sAPMNGI02DS(this);
   public sAPMNGI03DS APMNGI03DS = new sAPMNGI03DS(this);
   public sAPMNGI04DS APMNGI04DS = new sAPMNGI04DS(this);
   public sAPS36XDS APS36XDS = new sAPS36XDS(this);
   public sAPS905DS APS905DS = new sAPS905DS(this);
   public sGLS040DS GLS040DS = new sGLS040DS(this);

   public sCRS428DS CRS428DS = new sCRS428DS(this);
   public sCRS428PDS CRS428PDS = new sCRS428PDS(this);

   public sDSCUCD DSCUCD = new sDSCUCD(this);
   public sDSFFNC DSFFNC = new sDSFFNC(this);
   public sDSSDAP DSSDAP = new sDSSDAP(this);
   public sDSVTCD DSVTCD = new sDSVTCD(this);
   public sDSPYME DSPYME = new sDSPYME(this);
   public sDSPYTP DSPYTP = new sDSPYTP(this);
   public sCRMessageDS CRMessageDS = new sCRMessageDS(this);

   public cMoveInvoiceNo MoveInvoiceNo = new cMoveInvoiceNo(this); 
   public cCRCommon CRCommon = new cCRCommon(this);
   public cCROutput CROutput = new cCROutput(this);
   public MvxString PXBJNO = cRefBJNO.likeDef();
   public MvxString saveBJNO = cRefBJNO.likeDef();
   public MvxString XXECAR = new MvxString(2);
   public MvxString XXFTCO = cRefFTCO.likeDef();
   public MvxString XXBSCD = cRefBSCD.likeDef();
   public MvxString currentVSER = cRefVSER.likeDef();
   public MvxString currentVDSC = new MvxString(7);
   public MvxString CMNDIV_DIVI = cRefDIVI.likeDef();
   public MvxString CRS750DS_DIVI = cRefDIVI.likeDef();
   public MvxString XXLOCD = cRefCUCD.likeDef();
   public MvxString XXFEID = cRefFEID.likeDef();
   public MvxString XXEVEN = cRefEVEN.likeDef();
   public MvxString XXDNOI = cRefDNOI.likeDef();
   public MvxString invoiceClass = new MvxString(5);
   public MvxString foundParam_CIDVEN = new MvxString(1);
   public MvxString foundParam_CSUDIV = new MvxString(1);
   public MvxString XFDS = new MvxString(224);
   public MvxString X3DS = new MvxString(242);
   public MvxString X4DS = new MvxString(242);
   public MvxString X5DS = new MvxString(210);
   public MvxString X8DS = new MvxString(15);
   public MvxString paramQCMD = cRefQCMD.likeDef();
   public MvxString extraInfo450 = cRefGEXI.likeDef();
   public MvxString extraInfo451 = cRefGEXI.likeDef();
   public MvxString extraInfo452 = cRefGEXI.likeDef();
   public MvxString xOutGEXI = cRefGEXI.likeDef();
   public MvxString alphaYEA4 = new MvxString(4); 
   public MvxString previousCorr = new MvxString(24);
   public MvxString XXAIT1 = new MvxString(8); 	
   public MvxString XXAIT2 = new MvxString(8); 	
   public MvxString XXAIT3 = new MvxString(8); 	
   public MvxString XXAIT4 = new MvxString(8); 	
   public MvxString XXAIT5 = new MvxString(8); 	
   public MvxString XXAIT6 = new MvxString(8); 	
   public MvxString XXAIT7 = new MvxString(8); 	
   
   public double XAVTA1;
   public double XAVTA2;
   public double XCVTA1;
   public double XCVTA2;
   public double XXCUAM;
   public double X1CUAM;
   public double X1ACAM;
   public double XXACAM;
   public double XTACAM;
   public double XTCUAM;
   public double XXCDAM;
   public double XXPYTO;
   public double XXVTAM;
   public double XCDIFF;
   public double XSLASK;
   public double XSLAS1;
   public double XSLAS2;
   public double XXIVCU;
   public double X5IVNA;
   public double X2CUAM;
   public double X2ACAM;
   public double XXTLPR;
   public double evaluateAmount;
   public double VATBaseAmount;
   public double clearingAmount;
   public double XPVTP1;
   public double currencyRatePR;
   public double XDVTMX;//*LIKE XXN299
   public double XXVTMX;//*LIKE XXN299
   public double XXDDVA;//*LIKE XXN299
   public double XVVTDX;//*LIKE XXN54
   public double XXVTDX;//*LIKE XXN54   

   public double[] RVAT = new double[99]; 	 	 	
   public double[] VTP1 = new double[99]; 	 	 	
   public double[] GAMT = new double[99];
   public double[] VTD1 = new double[99];

   public int XXTRNO;
   public int currentBCHN;
   public int voucherTextCode_VTXC;
   public int cashDiscountMethod_CDGN;
   public int automaticVATaccounting_AVAT;
   public int notApprovedForPayment_PRDE;
   public int currentVONO;
   public int currentACYP;
   public int currentACDT;
   public int IBOP;
   public int XXCVAT;
   public int XXVTCD;
   public int XXEUVT;
   public int VATT08;
   public int XRDATE;
   public int XXDMCU;
   public int XXLCDC;
   public int X1LCDC;
   public int XXPTFA;
   public int XXFNCN;
   public int XXAPRV;
   public int XPYEA4;
   public int CMNDIV_CONO;
   public int CRS750DS_CONO;
   public int paymentClass;
   public int IX;
   public int actualTRNO;
   public int X2TRNO;
   public int lastUsedTRNO;
   public int original_YEA4; 	
   public int original_JRNO; 	
   public int original_JSNO; 	
   public int previousInyr; 
   public int previousVTCD;
   public int previous_VTCD;

   public boolean found_CMNDIV;
   public boolean found_CRS750DS;
   public boolean found_CSYTAB_CUCD;
   public boolean found_CSYTAB_VTCD;
   public boolean found_CSYTAB_SDAP;
   public boolean found_CSYTAB_PYME;
   public boolean found_CSYTAB_FFNC;
   public boolean found_CSYPAR_APS905;
   public boolean found_FCHACC;
   public boolean found_CIDMAS;
   public boolean found_CIDVEN;
   public boolean found_CSUDIV;
   public boolean found_MPCELE;
   public boolean found_FAPIBH;
   public boolean found_CMNCMP;
   public boolean found_MPSUPS;
   public boolean getInvoiceClass;
   public boolean comingFromCLEADD;
   public boolean recordOK;
   public boolean noVATClearAcc;
   public boolean manualCall; // Submitted manually from APS455 (i.e. not from some batch job or MI-program)
   public boolean found_MPAGRS;
   public boolean existExtraInfoBath;
   public boolean existExtraInfoTrans;
   public boolean multipleVTCD; 	
   public boolean computeVAT;
   public boolean unbalancedVoucher;
   public boolean writeVATRecord;
   public boolean market_RU;
   public boolean hasVATLine = false;
   public boolean deductableVAT;
   
   public cCRCalendar CRCalendar = new cCRCalendar(this);
   public sPPRTVQTYDS PPRTVQTYDS = new sPPRTVQTYDS(this);
   public MvxString XFPGNM = cRefPGNM.likeDef();

   public MvxRecord rAPS456 = new MvxRecord();

   public MvxRecord pPPRTVQTY = new MvxRecord();

   public void pPPRTVQTYpreCall() {
      pPPRTVQTY.reset();
      pPPRTVQTY.set(PPRTVQTYDS.getPPRTVQTYDS());
   }

   public void pPPRTVQTYpostCall() {
      pPPRTVQTY.reset();
      pPPRTVQTY.getString(PPRTVQTYDS.setPPRTVQTYDS());
   }
   
   public MvxString PX4XDS = new MvxString(72);   
   
   public MvxString alpha7 = new MvxString(7);
   
   public double XAVTA3;
   public double XCVTA3;
   
   public boolean supVNROexists;
   
   public MvxString supVRNO = new MvxString(16);   
   
   public MvxString savedAIT1_Cost = new MvxString(8);
   public MvxString savedAIT2_Cost = new MvxString(8);
   public MvxString savedAIT3_Cost = new MvxString(8);
   public MvxString savedAIT4_Cost = new MvxString(8);
   public MvxString savedAIT5_Cost = new MvxString(8);
   public MvxString savedAIT6_Cost = new MvxString(8);
   public MvxString savedAIT7_Cost = new MvxString(8);
   
   public MvxString savedAIT1_VAT1 = new MvxString(8);
   public MvxString savedAIT2_VAT1 = new MvxString(8);
   public MvxString savedAIT3_VAT1 = new MvxString(8);
   public MvxString savedAIT4_VAT1 = new MvxString(8);
   public MvxString savedAIT5_VAT1 = new MvxString(8);
   public MvxString savedAIT6_VAT1 = new MvxString(8);
   public MvxString savedAIT7_VAT1 = new MvxString(8);
   
   public MvxString savedAIT1_VAT2 = new MvxString(8);
   public MvxString savedAIT2_VAT2 = new MvxString(8);
   public MvxString savedAIT3_VAT2 = new MvxString(8);
   public MvxString savedAIT4_VAT2 = new MvxString(8);
   public MvxString savedAIT5_VAT2 = new MvxString(8);
   public MvxString savedAIT6_VAT2 = new MvxString(8);
   public MvxString savedAIT7_VAT2 = new MvxString(8);
   
   public double XXCDED;
   public double XXADED;
   public double XXCVT1;
   public double XXCVT2;
   
   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(JBCMD);
      v.addElement(APIBH);
      v.addElement(APIBL);
      v.addElement(MNDIV);
      v.addElement(SYPAR);
      v.addElement(SYTAB);
      v.addElement(IDMAS);
      v.addElement(IDVEN);
      v.addElement(SUDIV);
      v.addElement(CR040);
      v.addElement(PFAMF);
      v.addElement(PLINE);
      v.addElement(PCELE);
      v.addElement(PSUPR);
      v.addElement(PCASH);
      v.addElement(IDADR);
      v.addElement(GINHE);
      v.addElement(GINHC);
      v.addElement(GINLI);
      v.addElement(GINDH);
      v.addElement(GINPA);
      v.addElement(CHACC);
      v.addElement(MNCMP);
      v.addElement(PLEDG);
      v.addElement(PLEDX);
      v.addElement(GLEDG);
      v.addElement(PPPAY);
      v.addElement(VATPC);
      v.addElement(PSUPS);
      v.addElement(CRCommon);
      v.addElement(CROutput);
      v.addElement(PXBJNO);
      v.addElement(XXECAR);
      v.addElement(XXFTCO);
      v.addElement(XXBSCD);
      v.addElement(XXLOCD);
      v.addElement(XXFEID);
      v.addElement(XXEVEN);
      v.addElement(XXDNOI);
      v.addElement(currentVDSC);
      v.addElement(invoiceClass);
      v.addElement(foundParam_CIDVEN);
      v.addElement(foundParam_CSUDIV);
      v.addElement(CMNDIV_DIVI);
      v.addElement(CRS750DS_DIVI);
      v.addElement(APS455DS);
      v.addElement(APS905DS);
      v.addElement(CRS750DS);
      v.addElement(APS36XDS);
      v.addElement(MNS213DS);
      v.addElement(CRS428DS);
      v.addElement(CRS428PDS);
      v.addElement(DSFFNC);
      v.addElement(DSCUCD);
      v.addElement(DSVTCD);
      v.addElement(DSSDAP);
      v.addElement(DSPYME);
      v.addElement(DSPYTP);
      v.addElement(GLS040DS);
      v.addElement(PLCHKIF);
      v.addElement(PLCLCCU);
      v.addElement(PXCRTVAC);
      v.addElement(PXCHKTAB);
      v.addElement(PLCHKVO);
      v.addElement(PLCRTVT);
      v.addElement(PLRTVFNC);
      v.addElement(PXMNS210);
      v.addElement(PXCHKOBJ);
      v.addElement(pAPS450Fnc_lockForPrint);
      v.addElement(pAPS450Fnc_lockForUpdAPL);
      v.addElement(pAPS450Fnc_delete);
      v.addElement(pAPS450Fnc_unlock);
      v.addElement(pAPS450Fnc_validateInv);
      v.addElement(rGLS040_BJNO);
      v.addElement(rXXRE20);
      v.addElement(rRATE991);
      v.addElement(rXXVTXT);
      v.addElement(rXVCFI1);
      v.addElement(rDSINS0);
      v.addElement(rFESTKY);
      v.addElement(rXXInvInfo);
      v.addElement(rKSTR);
      v.addElement(rAPIDS);
      v.addElement(saveBJNO);
      v.addElement(XFDS);
      v.addElement(X3DS);
      v.addElement(X4DS);
      v.addElement(X5DS);
      v.addElement(X8DS);
      v.addElement(paramQCMD);
      v.addElement(extraInfo450);
      v.addElement(extraInfo451);
      v.addElement(extraInfo452);
      v.addElement(alphaYEA4);
      v.addElement(previousCorr);
      v.addElement(XXAIT1); 	
      v.addElement(XXAIT2); 	
      v.addElement(XXAIT3); 	
      v.addElement(XXAIT4); 	
      v.addElement(XXAIT5); 	
      v.addElement(XXAIT6); 	
      v.addElement(XXAIT7);
      v.addElement(xOutGEXI);
      v.addElement(APMNGI01DS);
      v.addElement(APMNGI02DS);
      v.addElement(APMNGI03DS);
      v.addElement(APMNGI04DS);
      v.addElement(RVAT);
      v.addElement(VTP1);
      v.addElement(GAMT);
      v.addElement(VTD1);
      v.addElement(CRMessageDS);
      v.addElement(currentVSER);
      v.addElement(GINLC);
      v.addElement(MoveInvoiceNo);
      v.addElement(PAGRS);
      v.addElement(CRCalendar);
      v.addElement(PPRTVQTYDS);
      v.addElement(XFPGNM);
      v.addElement(GLMNG04XDS);
      v.addElement(PX4XDS);      
      v.addElement(alpha7);
      v.addElement(supVRNO);
      v.addElement(savedAIT1_Cost);    
      v.addElement(savedAIT2_Cost);    
      v.addElement(savedAIT3_Cost);    
      v.addElement(savedAIT4_Cost);    
      v.addElement(savedAIT5_Cost);    
      v.addElement(savedAIT6_Cost);    
      v.addElement(savedAIT7_Cost);    
      v.addElement(savedAIT1_VAT1);    
      v.addElement(savedAIT2_VAT1);    
      v.addElement(savedAIT3_VAT1);    
      v.addElement(savedAIT4_VAT1);    
      v.addElement(savedAIT5_VAT1);    
      v.addElement(savedAIT6_VAT1);    
      v.addElement(savedAIT7_VAT1);    
      v.addElement(savedAIT1_VAT2);    
      v.addElement(savedAIT2_VAT2);    
      v.addElement(savedAIT3_VAT2);    
      v.addElement(savedAIT4_VAT2);    
      v.addElement(savedAIT5_VAT2);    
      v.addElement(savedAIT6_VAT2);    
      v.addElement(savedAIT7_VAT2);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
      IBOP = 0;
      XXCVAT = 0;
      XXVTCD = 0;
      XXEUVT = 0;
      VATT08 = 0;
      XXLCDC = 0;
      X1LCDC = 0;
      XXDMCU = 0;
      XXPTFA = 0;
      CMNDIV_CONO = 0;
      CRS750DS_CONO = 0;
      original_YEA4 = 0;
      original_JRNO = 0;
      original_JSNO = 0;
      previousInyr = 0;
      previousVTCD = 0;
      XCDIFF = 0D;
      XAVTA1 = 0D;
      XAVTA2 = 0D;
      XCVTA1 = 0D;
      XCVTA2 = 0D;
      XXCUAM = 0D;
      X1CUAM = 0D;
      XXTRNO = 0;
      XXPTFA = 0;
      XRDATE = 0;
      XTACAM = 0D;
      XXCDAM = 0D;
      XXPYTO = 0D;
      currentBCHN = 0;
      XXACAM = 0D;
      XXVTAM = 0D;
      X1ACAM = 0D;
      XTCUAM = 0D;
      XXFNCN = 0;
      XXAPRV = 0;
      XXTLPR = 0D;
      XPYEA4 = 0;
      XSLASK = 0D;
      XSLAS1 = 0D;
      XSLAS2 = 0D;
      XXIVCU = 0D;
      X5IVNA = 0D;
      paymentClass = 0; 	
      IX = 0; 	 	 	
      VATBaseAmount = 0D; 	 	 	 	
      clearingAmount = 0D; 	 	 	
      XPVTP1 = 0D; 	 	 	 	
      voucherTextCode_VTXC = 0; 	
      cashDiscountMethod_CDGN = 0; 	
      automaticVATaccounting_AVAT = 0;
      notApprovedForPayment_PRDE = 0;
      currentACYP = 0;
      currentACDT = 0;
      X2CUAM = 0D; 	
      X2ACAM = 0D; 	
      currentVONO = 0;
      evaluateAmount = 0D;
      actualTRNO = 0;
      X2TRNO = 0;
      lastUsedTRNO = 0;
      previous_VTCD = 0;
      currencyRatePR = 0d;
      XDVTMX = 0d;
      XXVTMX = 0d;
      XXDDVA = 0d;
      XVVTDX = 0d;
      XXVTDX = 0d;      
      getInvoiceClass = false; 	
      found_CMNDIV = false;
      found_CSYTAB_CUCD = false;
      found_CIDVEN = false;
      found_CSUDIV = false;
      noVATClearAcc = false;
      getInvoiceClass = false;
      found_CRS750DS = false;
      found_CSYTAB_VTCD = false;
      found_CSYTAB_SDAP = false;
      found_CSYTAB_PYME = false;
      found_CSYTAB_FFNC = false;
      found_CSYPAR_APS905 = false;
      found_FCHACC = false;
      found_CIDMAS = false;
      found_MPCELE = false;
      found_FAPIBH = false;
      found_CMNCMP = false;
      found_MPSUPS = false;
      comingFromCLEADD = false;
      recordOK = false;
      manualCall = false;
      found_MPAGRS = false;
      existExtraInfoBath = false;
      existExtraInfoTrans = false;
      multipleVTCD = false; 	
      computeVAT = false;
      unbalancedVoucher = false; 
      writeVATRecord = false;
      market_RU = false;
      hasVATLine = false;
      deductableVAT = false;
      XAVTA3 = 0d;
      XCVTA3 = 0d;
      supVNROexists = false;
      XXCDED = 0d;
      XXADED = 0d;
      XXCVT1 = 0d;
      XXCVT2 = 0d;
   }

public final static String _version="15";

public final static String _release="1";

public final static String _spLevel="2";

public final static String _spNumber="";

public final static String _GUID="96E5B26BD8BB4977A2046815D47BA953";

public final static String _tempFixComment="";

public final static String _build="000000000000199";

public final static String _pgmName="APS455Sbm";

   public String getVersion() {
      return _version;
   } //·end of method getVersion

   public String getRelease() {
      return _release;
   } //·end of method getRelease

   public String getSpLevel() {
      return _spLevel;
   } //·end of method getSpLevel

   public String getSpNumber() {
      return _spNumber;
   } //·end of method getSpNumber

   public String getGUID() {
      return _GUID;
   } //·end of method getGUID

   public String getTempFixComment() {
      return _tempFixComment;
   } //·end of method getTempFixComment

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   } //·end of method getVersionInformation

   public String getBuild() {
      return (_version + _release + _build + "      " +  _pgmName + "                                   ").substring(0,34);
   } //·end of method getBuild

   public String [][] getStandardModification() {
      return _standardModifications;
   } //·end of method [][] getStandardModification

  public final static String [][] _standardModifications={};
}
